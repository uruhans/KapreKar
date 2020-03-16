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
      .delay(500, TimeUnit.MILLISECONDS)
      .flatMap { bleConnectionState ->
        // we are already connected
        if (bleConnectionState == BleConnectionState.CONNECTED || bleConnectionState == BleConnectionState.CONNECTING) {
          // find and return the connected device
          bluetoothManager.connectedDeviceObservable(macAddress)
            .map { bleConnectionState }
        }
        else {
          // do actual connection to the new device
          bluetoothManager.connectBleDevice(macAddress)
            .delay(3, TimeUnit.SECONDS)
            .map { bleConnectionState }
        }
      }
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
            Observable.timer(5.0.pow(retryCount).toLong(), TimeUnit.SECONDS)
              .doOnNext { Timber.d("Now we retry: $retryCount") }
          }
      }
      .filter { it == BleConnectionState.CONNECTED }
      // i need multiple subscribers on this particular one
      .publish()
      .autoConnect()
    
    val notificationSubscriptionObservable = connectedBleDeviceObservable
      // this one subscribes to notifications and returns a peripheral object containing the latest state of the device
      .flatMap { bluetoothManager.bleDeviceResultObservable(macAddress) }
      // map it to an actual device update
      .map { DeviceResult.DeviceUpdateResult(it) }
    
    val rawDataPackageObservable = connectedBleDeviceObservable
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