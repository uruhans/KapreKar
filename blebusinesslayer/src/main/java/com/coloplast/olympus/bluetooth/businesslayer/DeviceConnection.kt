package com.coloplast.olympus.bluetooth.businesslayer

import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class DeviceConnection(private val bluetoothManager: BluetoothManager) {
  
  private val maxConnectionAttempts = 3
  
  fun subscribeForNotifications(macAddress: String): Observable<DeviceResult> {
    // connect to specific device or use the connection we already have
    val connectedBleDeviceObservable = bluetoothManager.bleConnectionStateObservable(macAddress)
      .take(1)
      .delay(500, TimeUnit.MILLISECONDS)
      .flatMap { bleConnectionState ->
        // we are already connected
        if(bleConnectionState == BleConnectionState.DISCONNECTED) {
          // do actual connection to the new device
          bluetoothManager.connectBleDevice(macAddress)
            .flatMap { bluetoothManager.connectedDeviceObservable(macAddress) }
            .map { it }
        }
        else {
          bluetoothManager.connectedDeviceObservable(macAddress)
        }
      }
      .filter { it.connectionState == BleConnectionState.CONNECTED }
      .filter { it.bondState == 12 }
      // continue to do so until the device gets disconnected
      // skip first because initial state is disconnected
      .takeUntil(
        bluetoothManager.bleDisconnectedObservable(macAddress)
          .skip(1)
      )
      .retryWhen { errors ->
        // zip the errors with a range (which represents the number of times to retry
        Observable.zip(
            errors,
            Observable.range(1, maxConnectionAttempts),
            BiFunction { _: Throwable, retryCount: Int -> retryCount }
          )
          .doOnNext { Timber.d("retry: $it") }
          // retry after 5 seconds (with exponential backoff on each retry)
          .flatMap { retryCount ->
            Observable.timer(
                2.0.pow(retryCount)
                  .toLong(), TimeUnit.SECONDS
              )
              .doOnNext { Timber.d("Now we retry: $retryCount") }
          }
      }
      .doOnNext { Timber.e("$it") }
      .filter { it.isConnected() }
      .doOnNext { Timber.e("onNext was hit") }
      // i need multiple subscribers on this particular one
      .publish()
      .autoConnect()
    
    val notificationSubscriptionObservable = connectedBleDeviceObservable
      .doOnNext{ Timber.d("notificationSubscriptionObservable") }
      // this one subscribes to notifications and returns a peripheral object containing the latest state of the device
      .flatMap { bluetoothManager.bleDeviceResultObservable(macAddress) }
      // map it to an actual device update
      .map { DeviceResult.DeviceUpdateResult(it) }
    
    val rawDataPackageObservable = connectedBleDeviceObservable
      .doOnNext{ Timber.d("rawDataPackageObservable") }
      // subscribe to raw data packages
      .flatMap { bluetoothManager.setupOlympusRawDataPackageNotifications(macAddress) }
      // and map to a raw data result
      .map { DeviceResult.RawDataPackageResult(it) }
    
    // finally merge and return result
    return Observable.merge(
      notificationSubscriptionObservable,
      rawDataPackageObservable
    )
  }
}