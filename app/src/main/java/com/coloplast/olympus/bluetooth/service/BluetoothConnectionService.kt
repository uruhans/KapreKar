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
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class BluetoothConnectionService : Service(), KoinComponent, BluetoothServiceResult<BluetoothServiceModel> {
  
  private val viewModel: BluetoothServiceViewModel by inject()
  private val notificationManager: NotificationManager by inject()
  
  private val olympusNotificationManager: OlympusNotificationManager by inject()
  
  private val compositeDisposable = CompositeDisposable()
  
  override fun onBind(intent: Intent?): IBinder? {
    return null
  }
  
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    IntentFilter().let {
      it.addAction(INTENT_FILTER_STOP_SERVICE)
      registerReceiver(intentReceiver, it)
    }
    
    startForeground(1212, getServiceNotification())
    
    compositeDisposable.add(viewModel.bind(this))
    return START_STICKY
  }
  
  override fun onResult(): Function<Observable<BluetoothServiceModel>, Disposable> {
    return RxUi.ui {
      Timber.d("$it")
    
      when(it) {
        is BluetoothServiceModel.DeviceUpdate -> {
          // this will allow for notifications to be send when app is in the background
          olympusNotificationManager.send(it.statusChange.state)
          
          // this will dispatch the state change to any who observes it (none if app is in background)
          StateDispatcher.stateChangeRelay.accept(it.statusChange)
        }
      }
    }
  }
  
  private val intentReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (intent?.action == INTENT_FILTER_STOP_SERVICE) {
        killService()
      }
    }
  }
  
  private fun getServiceNotification(): Notification {
    createNotificationChannel()
    
    val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
      PendingIntent.getActivity(this, 0, notificationIntent, 0)
    }
    
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      notificationManager.deleteNotificationChannel("2")
    }
    
    // only create the notification channel if we are above API 25 and the channel doesn't already exist
    if (Build.VERSION.SDK_INT >= 26 && notificationManager.getNotificationChannel("2") == null) {
      NotificationChannel(
        "2", "Service Notifications", NotificationManager.IMPORTANCE_DEFAULT
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
    unregisterReceiver(intentReceiver)
    stopForeground(true)
    stopSelf()
  }
  
  companion object {
    const val INTENT_FILTER_STOP_SERVICE = "${BuildConfig.APPLICATION_ID}.BLUETOOTH.STOP_SERVICE"
  }
}