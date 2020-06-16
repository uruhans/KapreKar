package com.coloplast.olympus.bluetooth.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.artemzin.rxui.RxUi
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.R
import com.coloplast.olympus.activities.MainActivity
import com.coloplast.olympus.bluetooth.BluetoothServiceViewModel
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.model.BluetoothServiceModel
import com.coloplast.olympus.bluetooth.model.BluetoothServiceResult
import com.coloplast.olympus.notifications.OlympusNotificationManager
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
  private val compositeDisposable = CompositeDisposable()

  private val channelId: String by lazy {  applicationContext.getString(R.string.lock_service_notification_channel_id) }
  private val channelName: String by lazy { applicationContext.getString(R.string.lock_service_notification_channel_name) }
  private val channelDescription: String by lazy { applicationContext.getString(R.string.lock_service_notification_channel_description) }

  private val ringingAlertChannelId: String by lazy { applicationContext.getString(R.string.lock_ringing_alarm_notification_channel_id) }
  private val ringingAlertChannelName: String by lazy { applicationContext.getString(R.string.lock_ringing_alarm_notification_channel_name) }
  private val ringingAlertChannelDescription: String by lazy { applicationContext.getString(R.string.lock_ringing_alarm_notification_channel_description) }
  
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
    return START_STICKY
  }
  
  override fun onResult(): Function<Observable<BluetoothServiceModel>, Disposable> {
    return RxUi.ui {
      Timber.d("$it")
      
      when (it) {
        is BluetoothServiceModel.DeviceUpdate -> {
          if(it.statusChange.state == UiState.SOMETHING_ELSE) {
            return@ui
          }

          // this will allow for notifications to be send when app is in the background
          olympusNotificationManager.send(it.statusChange.state)
          
          // this will dispatch the state change to any who observes it (none if app is in background)
          StateDispatcher.dispatch(it.statusChange)
        }
        is BluetoothServiceModel.RawDataPackageUpdate -> {
          // TODO - this may need to change if subscribers want rawpackages?
          if(it.fromActiveTx) {
            StateDispatcher.dispatch(it.macAddress)
          }
        }
        is BluetoothServiceModel.Disconnected -> {
          // check to see if we have a stateChange in the uiModel
          // this will be used to notify the Ui to change state (so we dont get stuck in multiple tx state or other state based on disconnected tx)
          it.otherTxStateChange?.let { stateChange ->
            StateDispatcher.dispatch(stateChange)
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
    
    val notification = NotificationCompat.Builder(this, channelId)
      .setContentTitle(channelName)
      .setContentText(channelDescription)
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
    if (Build.VERSION.SDK_INT >= 26 && notificationManager.getNotificationChannel(channelId) == null) {
      // Olympus Bluetooth Connection notification channel
      NotificationChannel(
        channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = channelDescription
        enableVibration(false)
        enableLights(false)
        lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(this)
      }

      // Ringing alert (service) notification channel.
      NotificationChannel(
        ringingAlertChannelId, ringingAlertChannelName, NotificationManager.IMPORTANCE_HIGH
      ).apply {
        val ringtoneUri = Settings.System.DEFAULT_RINGTONE_URI
        val audioAttributes =
          AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
        description = ringingAlertChannelDescription
        enableVibration(false)
        enableLights(false)
        lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        setSound(ringtoneUri, audioAttributes)
        notificationManager.createNotificationChannel(this)
      }
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