package com.coloplast.olympus.bluetooth

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.artemzin.rxui.RxUi
import com.coloplast.olympus.R
import com.coloplast.olympus.activities.MainActivity
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.feature.home.HomeView
import com.coloplast.olympus.ui.feature.home.HomeViewModel
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class BluetoothConnectionService : Service(), KoinComponent, HomeView {
  
  private val notificationManager: NotificationManager by inject()
  private val viewModel : HomeViewModel by inject()
  
  private val connectRelay = PublishRelay.create<String>()
  private val scanRelay = PublishRelay.create<Unit>()
  
  private val compositeDisposable = CompositeDisposable()
  
  private val transmitterComponent: TransmitterComponent by inject()
  
  override fun onBind(intent: Intent?): IBinder? {
    return null
  }
  
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    IntentFilter().let {
      it.addAction("STOP_SERVICE")
      registerReceiver(intentReceiver, it)
    }
    
    startForeground(1212, getServiceNotification())
    
    compositeDisposable.add(viewModel.connectBleDevice(this))
    
    compositeDisposable.add(
      transmitterComponent.getTransmitters()
        .filter { it is DataResult.Success<*> }
        .map { it as DataResult.Success<*> }
        .map { it.data as List<*> }
        .flatMapIterable { it }
        .delay(3, TimeUnit.SECONDS)
        .distinctUntilChanged()
        .map { it as Transmitter }
        .map { transmitter ->
          connectRelay.accept(transmitter.macAddress)
          transmitter
        }
        .subscribe { Timber.d("$it") }
    )
    
    return START_STICKY
  }
  
  override fun onScanRequested(): Observable<Unit> {
    return scanRelay.publish().refCount()
  }
  
  override fun onConnectPeripheral(): Observable<String> {
    return connectRelay.publish().refCount()
  }
  
  override fun render(): Function<Observable<HomeUiModel>, Disposable> {
    return RxUi.ui {
      Timber.d("$it")
    }
  }
  
  private val intentReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (intent?.action == "STOP_SERVICE") {
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
}