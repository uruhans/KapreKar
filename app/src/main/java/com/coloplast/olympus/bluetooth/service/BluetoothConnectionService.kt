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
import com.coloplast.olympus.bluetooth.ServiceDispatcher
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.ServiceStateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.model.*
import com.coloplast.olympus.notifications.OlympusNotificationManager
import com.coloplast.olympus.patchsplit.models.CompromisedVault
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
  private val serviceStateDispatcher: ServiceStateDispatcher by inject()
  private val compromisedVault: CompromisedVault by inject()
  private val bluetoothServiceHelper: BluetoothServiceHelper by inject()

  private val compositeDisposable = CompositeDisposable()

  private val bluetoothConnectionChannelId: String by lazy { applicationContext.getString(R.string.lock_service_notification_channel_id) }
  private val bluetoothConnectionChannelName: String by lazy { applicationContext.getString(R.string.lock_service_notification_channel_name) }
  private val bluetoothConnectionChannelDescription: String by lazy { applicationContext.getString(R.string.lock_service_notification_channel_description) }

  private val connectionAttempts = mutableMapOf<String, Int>()

  override fun onBind(intent: Intent?): IBinder? {
    // we are not binding to this service, so just return null
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    return startService()
  }

  private fun startService(): Int {
    isRunning = true

    IntentFilter().let {
      it.addAction(INTENT_FILTER_STOP_SERVICE)
      it.addAction(INTENT_FILTER_INITIATE_RESET)
      registerReceiver(intentReceiver, it)
    }

    // start the actual foreground service (a permanent notification is required to inform the user of it)
    startForeground(2125, getServiceNotification())

    compositeDisposable.add(get<BluetoothServiceViewModel>().bind(this))
    compositeDisposable.add(
      serviceStateDispatcher.serviceStateObservable()
        .filter { it == ServiceStateDispatcher.ServiceState.STOP_SERVICE }
        .subscribe { killService() }
    )

    return START_STICKY
  }

  override fun onResult(): Function<Observable<BluetoothServiceModel>, Disposable> {
    return RxUi.ui {
      Timber.d("$it")

      when (it) {
        is BluetoothServiceModel.FirmwareUpdateAvailable -> {
          if(transmitterManager.isActiveTransmitter(it.statusChange.getMacAddress())) {
            olympusNotificationManager.send(it.statusChange.getMacAddress(), UiState.FIRMWARE_AVAILABLE)
            StateDispatcher.dispatchStateForActiveTransmitter(it.statusChange)
          }

          // the reason we are dispatching this too, is because we might be in the onboarding flow
          // which will prevent the user from getting to the status screen if this isn't here
          StateDispatcher.dispatch(it.statusChange)
        }
        is BluetoothServiceModel.AppUpdateAvailable -> {
          if(transmitterManager.isActiveTransmitter(it.statusChange.getMacAddress())) {

            olympusNotificationManager.send(it.statusChange.getMacAddress(), UiState.APP_UPDATE_AVAILABLE)
            StateDispatcher.dispatchStateForActiveTransmitter(it.statusChange)
          }

          // the reason we are dispatching this too, is because we might be in the onboarding flow
          // which will prevent the user from getting to the status screen if this isn't here
          StateDispatcher.dispatch(it.statusChange)
        }
        is BluetoothServiceModel.DeviceUpdate -> {
          // this will dispatch the state change to any who observes it (none if app is in background)
          StateDispatcher.dispatch(it.statusChange)

          val updatedStateChange = bluetoothServiceHelper.handleTransmitterUpdate(it.statusChange) ?: return@ui

          // this will allow for notifications to be send when app is in the background
          olympusNotificationManager.send(updatedStateChange.stateChange.getMacAddress(), updatedStateChange.stateChange.state)

          if(updatedStateChange.stateChange != it.statusChange) {
            StateDispatcher.dispatch(updatedStateChange.stateChange)
          }

          StateDispatcher.dispatchStateForActiveTransmitter(updatedStateChange.stateChange)
        }
        is BluetoothServiceModel.BatteryUpdate -> {
          StateDispatcher.dispatch(it.statusChange)

          if(transmitterManager.isActiveTransmitter(it.statusChange.getMacAddress())) {
            olympusNotificationManager.send(it.statusChange.getMacAddress(), it.statusChange.state)
            olympusNotificationManager.send(it.statusChange.getMacAddress(), it.statusChange.overlayState)

            // this has been removed, because it was causing issues with the status screen
            // ie: deviceUpdate comes with UiState.MultipleTransmitters - but then the batteryUpdate
            // who doesn't know about multiple transmitters overrides it with *this* particular statusUpdate
            // causing the home screen to show the wrong status
            // StateDispatcher.dispatchStateForActiveTransmitter(it.statusChange)
          }
        }
        is BluetoothServiceModel.RawDataPackageUpdate -> {
          val activeTransmitter = transmitterManager.isActiveTransmitter(it.macAddress)

          if (activeTransmitter) {
            StateDispatcher.dispatch(it.macAddress)
          }
        }
        is BluetoothServiceModel.CompromisedPackageUpdate -> {
          // only add the running compromised value to the vault if it's coming from
          // the currently active transmitter
          if(transmitterManager.isActiveTransmitter(it.macAddress)) {
            compromisedVault.set(it.compromisedPackage.running, it.lastUpdate)
          }
        }
        is BluetoothServiceModel.Disconnected -> {
          connectionAttempts[it.macAddress] = connectionAttempts[it.macAddress]?.plus(1) ?: 1

          val latestState = transmitterManager.getLatestState(it.macAddress)
          val bleDevice = latestState?.bleDevice ?: BleDevice("", it.macAddress, BleConnectionState.DISCONNECTED)
          bleDevice.connectionState = BleConnectionState.DISCONNECTED

          StateDispatcher.dispatch(StateChange(UiState.DISCONNECTED, bleDevice = bleDevice))

          if((transmitterManager.isActiveTransmitter(it.macAddress) || transmitterManager.isOnlyTransmitterAdded(it.macAddress)) && connectionAttempts[it.macAddress]!! >= 3) {
            connectionAttempts[it.macAddress] = 0

            val disconnectedStateChange = StateChange(UiState.TRANSMITTER_NOT_FOUND, bleDevice = bleDevice)
            StateDispatcher.dispatchStateForActiveTransmitter(disconnectedStateChange)
            return@ui
          }

          val otherTxStateChange = transmitterManager.onTransmitterDisconnect(it.macAddress)

          if(transmitterManager.isActiveTransmitter(it.macAddress) && latestState != null) {
            olympusNotificationManager.send(it.macAddress, OverlayState.BLE_CONNECTION_LOST)
            StateDispatcher.dispatchStateForActiveTransmitter(StateChange(latestState.state, OverlayState.BLE_CONNECTION_LOST, bleDevice))
          }
          else if(otherTxStateChange != null) {
            StateDispatcher.dispatchStateForActiveTransmitter(otherTxStateChange)
          }

          ServiceDispatcher.dispatchEvent(ServiceEvent.DisconnectTransmitter(it.macAddress))
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
    isRunning = false
    unregisterReceiver(intentReceiver)
    compositeDisposable.clear()
    stopForeground(true)
    stopSelf()
  }

  companion object {
    const val INTENT_FILTER_STOP_SERVICE = "${BuildConfig.APPLICATION_ID}.BLUETOOTH.STOP_SERVICE"
    const val INTENT_FILTER_INITIATE_RESET = "${BuildConfig.APPLICATION_ID}.BLUETOOTH.INTENT_FILTER_INITIATE_RESET"
    const val INTENT_FILTER_RESET_SERVICE = " ${BuildConfig.APPLICATION_ID}.BLUETOOTH.RESET_SERVICE"

    var isRunning = false
  }
}