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
import com.coloplast.olympus.bluetooth.model.BluetoothServiceModel
import com.coloplast.olympus.bluetooth.model.BluetoothServiceResult
import com.coloplast.olympus.notifications.OlympusNotificationManager
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

/**
 * A foreground service that connects and maintains connection to already added transmitters
 * - will try to reconnect after connection lost or out-of-range
 * - will also stay connected to transmitter even if app is in the background
 */
class BluetoothConnectionService : Service(), KoinComponent, BluetoothServiceResult<BluetoothServiceModel> {
  
  private val viewModel: BluetoothServiceViewModel by inject()
  private val notificationManager: NotificationManager by inject()
  
  private val olympusNotificationManager: OlympusNotificationManager by inject()
  
  private val compositeDisposable = CompositeDisposable()
  
  override fun onBind(intent: Intent?): IBinder? {
    // we are not binding to this service, so just return null
    return null
  }
  
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    // start the actual foreground service (a permanent notification is required to inform the user of it)
    startForeground(2125, getServiceNotification())
    
    compositeDisposable.add(viewModel.bind(this))
    return START_STICKY
  }
  
  override fun onResult(): Function<Observable<BluetoothServiceModel>, Disposable> {
    return RxUi.ui {
      Timber.d("$it")
    
      when(it) {
        is BluetoothServiceModel.DeviceUpdate -> {
          if(it.statusChange.state == UiState.NO_CHANGE && it.statusChange.overlayState == OverlayState.NONE) {
            return@ui
          }
          
          // this will allow for notifications to be send when app is in the background
          olympusNotificationManager.send(it.statusChange.state)
          
          // this will dispatch the state change to any who observes it (none if app is in background)
          StateDispatcher.dispatch(it.statusChange)
        }
      }
    }
  }
  
  private fun getServiceNotification(): Notification {
    createNotificationChannel()
    
    val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
      PendingIntent.getActivity(this, 0, notificationIntent, 0)
    }

    // TODO - remove hardcoded text
    val notification = NotificationCompat.Builder(this, "2")
      .setContentTitle("Olympus Bluetooth Connection")
      .setContentText("Maintaining transmitter connection")
      .setSmallIcon(R.drawable.ic_detailed_plate_ok)
      .setContentIntent(pendingIntent)
      .setPriority(NotificationCompat.PRIORITY_MIN)
      .setCategory(Notification.CATEGORY_SERVICE)
      .setOngoing(true)
    
    return notification.build()
  }
  
  private fun createNotificationChannel() {
    // only create the notification channel if we are above API 25 and the channel doesn't already exist
    if (Build.VERSION.SDK_INT >= 26 && notificationManager.getNotificationChannel("2") == null) {
      NotificationChannel(
        "2", "Service Notification", NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Notifies you when the background service is working"
        enableVibration(false)
        enableLights(false)
        lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(this)
      }
    }
  }
  
  private fun killService() {
    compositeDisposable.dispose()
    stopForeground(true)
    stopSelf()
  }
}