package com.coloplast.olympus.bluetooth.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.artemzin.rxui.RxUi
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.R
import com.coloplast.olympus.activities.MainActivity
import com.coloplast.olympus.bluetooth.BluetoothServiceViewModel
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.FirmwareManager
import com.coloplast.olympus.bluetooth.model.BluetoothServiceModel
import com.coloplast.olympus.bluetooth.model.BluetoothServiceResult
import com.coloplast.olympus.notifications.OlympusNotificationManager
import com.coloplast.olympus.ui.feature.TransmitterManager
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import timber.log.Timber

/**
 * A foreground service that connects and maintains connection to already added transmitters
 * - will try to reconnect after connection lost or out-of-range
 * - will also stay connected to transmitter even if app is in the background
 */
class BluetoothConnectionService : Service(), KoinComponent, BluetoothServiceResult<BluetoothServiceModel> {

  private val notificationManager: NotificationManager by inject()
  private val olympusNotificationManager: OlympusNotificationManager = get()
  private val transmitterManager: TransmitterManager by inject()
  private val firmwareManager: FirmwareManager by inject()

  private val compositeDisposable = CompositeDisposable()

  private val bluetoothConnectionChannelId: String by lazy { applicationContext.getString(R.string.lock_service_notification_channel_id) }
  private val bluetoothConnectionChannelName: String by lazy { applicationContext.getString(R.string.lock_service_notification_channel_name) }
  private val bluetoothConnectionChannelDescription: String by lazy { applicationContext.getString(R.string.lock_service_notification_channel_description) }

  override fun onBind(intent: Intent?): IBinder? {
    // we are not binding to this service, so just return null
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    return startService()
  }

  private fun startService(): Int {
    IntentFilter().let {
      it.addAction(INTENT_FILTER_STOP_SERVICE)
      it.addAction(INTENT_FILTER_INITIATE_RESET)
      registerReceiver(intentReceiver, it)
    }

    // start the actual foreground service (a permanent notification is required to inform the user of it)
    startForeground(2125, getServiceNotification())

    compositeDisposable.add(get<BluetoothServiceViewModel>().bind(this))
    compositeDisposable.add(
      firmwareManager.serviceStateObservable()
        .filter { it == FirmwareManager.ServiceState.STOP_SERVICE }
        .subscribe { killService() }
    )
    return START_STICKY
  }

  override fun onResult(): Function<Observable<BluetoothServiceModel>, Disposable> {
    return RxUi.ui {
      Timber.d("$it")

      when (it) {
        is BluetoothServiceModel.FirmwareUpdateAvailable -> {
          olympusNotificationManager.send(UiState.FIRMWARE_AVAILABLE)
          StateDispatcher.dispatchStateForActiveTransmitter(it.statusChange)
        }
        is BluetoothServiceModel.DeviceUpdate -> {
          if (it.statusChange.state == UiState.SOMETHING_ELSE) {
            return@ui
          }

          // this will dispatch the state change to any who observes it (none if app is in background)
          StateDispatcher.dispatch(it.statusChange)

          val updatedStateChange = transmitterManager.determineCurrentlyActiveTransmitterAndReturnStateChangeFromIt(it.statusChange)
          val newStateChange = transmitterManager.determineTransmitterPatchQuestioning(updatedStateChange)

          // TODO - this will result in multiple notifications sent on EVERY statechange on EVERY transmitter
          // this will allow for notifications to be send when app is in the background
          olympusNotificationManager.send(newStateChange.state)
          StateDispatcher.dispatchStateForActiveTransmitter(newStateChange)
        }
        is BluetoothServiceModel.RawDataPackageUpdate -> {
          val activeTransmitter = transmitterManager.isActiveTransmitter(it.macAddress)

          if (activeTransmitter) {
            StateDispatcher.dispatch(it.macAddress)
          }
        }
        is BluetoothServiceModel.Disconnected -> {
          // we have a disconnect - get the latest statechange from other tx (if needed and if possible)
          val otherTxStateChange = transmitterManager.onTransmitterDisconnect(it.macAddress)

          // this will be used to notify the Ui to change state (so we dont get stuck in multiple tx state or other state based on disconnected tx)
          otherTxStateChange?.let { stateChange ->
            if (stateChange.overlayState == OverlayState.BLE_CONNECTION_LOST) {
              olympusNotificationManager.send(OverlayState.BLE_CONNECTION_LOST)
            }

            StateDispatcher.dispatchStateForActiveTransmitter(stateChange)
          }
        }
        is BluetoothServiceModel.ScanningStarted -> {
          StateDispatcher.dispatchScanning()
        }
      }
    }
  }

  private fun getServiceNotification(): Notification {
    createNotificationChannel()

    val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
      PendingIntent.getActivity(this, 0, notificationIntent, 0)
    }

//    val resetPendingIntent: PendingIntent = Intent(INTENT_FILTER_INITIATE_RESET)
//      .let { resetIntent -> PendingIntent.getBroadcast(this, 0, resetIntent, 0) }

    val notification = NotificationCompat.Builder(this, bluetoothConnectionChannelId)
      .setContentTitle(bluetoothConnectionChannelName)
      .setContentText(bluetoothConnectionChannelDescription)
//      .addAction(R.drawable.ic_arrow, "Reset", resetPendingIntent)
      .setSmallIcon(R.drawable.ic_detailed_plate_ok)
      .setContentIntent(pendingIntent)
      .setPriority(NotificationCompat.PRIORITY_MIN)
      .setCategory(Notification.CATEGORY_SERVICE)
      .setOngoing(true)

    return notification.build()
  }

  private fun createNotificationChannel() {
    // only create the notification channel if we are above API 25 and the channel doesn't already exist
    if (Build.VERSION.SDK_INT >= 26 && notificationManager.getNotificationChannel(bluetoothConnectionChannelId) == null) {
      val bluetoothConnectionNotificationChannel = NotificationChannel(
        bluetoothConnectionChannelId, bluetoothConnectionChannelName, NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = bluetoothConnectionChannelDescription
        enableVibration(false)
        enableLights(false)
        lockscreenVisibility = Notification.VISIBILITY_PRIVATE
      }
      notificationManager.createNotificationChannel(bluetoothConnectionNotificationChannel)
    }
  }

  private val intentReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (intent?.action == INTENT_FILTER_STOP_SERVICE) {
        killService()
      }
      else if (intent?.action == INTENT_FILTER_INITIATE_RESET) {
        with(Intent(this@BluetoothConnectionService, ResetReceiver::class.java)
          .also { it.action = INTENT_FILTER_RESET_SERVICE }
        ) {
          sendBroadcast(this)
        }
      }
    }
  }

  private fun killService() {
    unregisterReceiver(intentReceiver)
    compositeDisposable.clear()
    stopForeground(true)
    stopSelf()
  }

  companion object {
    const val INTENT_FILTER_STOP_SERVICE = "${BuildConfig.APPLICATION_ID}.BLUETOOTH.STOP_SERVICE"
    const val INTENT_FILTER_INITIATE_RESET = "${BuildConfig.APPLICATION_ID}.BLUETOOTH.INTENT_FILTER_INITIATE_RESET"
    const val INTENT_FILTER_RESET_SERVICE = " ${BuildConfig.APPLICATION_ID}.BLUETOOTH.RESET_SERVICE"
    //    fun resetService(activity: Activity) {
    //      with(Intent(INTENT_FILTER_RESET_SERVICE)) {
    //        activity.sendBroadcast(this)
    //      }
    //    }
  }
}