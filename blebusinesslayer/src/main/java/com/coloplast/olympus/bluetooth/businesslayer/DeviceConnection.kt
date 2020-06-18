package com.coloplast.olympus.bluetooth.businesslayer

import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class DeviceConnection(private val bluetoothManager: BluetoothManager) {

  val disconnectRelay = PublishRelay.create<String>()

  fun subscribeForNotifications(macAddress: String): Observable<DeviceResult> {

    val disconnectObservable = disconnectRelay.publish().refCount().doOnNext { Timber.e("Disconnect relay was hit") }

    // connect to specific device or use the connection we already have
    val connectedBleDeviceObservable = bluetoothManager.bleConnectionStateObservable(macAddress)
      .take(1)
      .delay(500, TimeUnit.MILLISECONDS)
      .flatMap { bleConnectionState ->
        if(bleConnectionState == BleConnectionState.DISCONNECTED) {
          // do actual connection to the new device
          bluetoothManager.connectBleDevice(macAddress)
            .takeUntil(disconnectObservable)
            .flatMap { bluetoothManager.connectedDeviceObservable(macAddress) }
            .map { it }
        }
        else {
          // we are already connected, so grab that one
          bluetoothManager.connectedDeviceObservable(macAddress)
        }
      }
      // only continue if we are fully connected
      .filter { it.connectionState == BleConnectionState.CONNECTED }
//      .takeUntil(
//        // continue to do so until the device gets disconnected
//        // skip first because initial state is disconnected
//        bluetoothManager.bleDisconnectedObservable(macAddress)
//          .doOnNext { Timber.e("ConnectionState: $it") }
//          .skip(1)
//      )
//      .retryWhen { errors ->
//        // zip the errors with a range (which represents the number of times to retry)
//        Observable.zip(
//            errors,
//            Observable.range(1, maxConnectionAttempts),
//            BiFunction { _: Throwable, retryCount: Int -> retryCount }
//          )
//          .doOnNext { Timber.d("retry: $it") }
//          // retry after 2 seconds (with exponential backoff on each retry), 2 - 4 - 8 seconds
//          .flatMap { retryCount ->
//            Observable.timer(
//                2.0.pow(retryCount)
//                  .toLong(), TimeUnit.SECONDS
//              )
//          }
//      }
      .filter { it.isConnected() }
      // i need multiple subscribers on this particular one
      .publish()
      .autoConnect()
    
    val notificationSubscriptionObservable = connectedBleDeviceObservable
      // this one subscribes to notifications and returns a peripheral object containing the latest state of the device
      .flatMap {
        bluetoothManager.bleDeviceResultObservable(macAddress)
          .retryWhen{ it }
      }
      // map it to an actual device update
      .map { DeviceResult.DeviceUpdateResult(it) }
    
    val rawDataPackageObservable = connectedBleDeviceObservable
      // subscribe to raw data packages
      .flatMap {
        bluetoothManager.setupOlympusRawDataPackageNotifications(macAddress)
          .retryWhen{ it }
      }
      // and map to a raw data result
      .map { DeviceResult.RawDataPackageResult(macAddress, it) }
    
    // finally merge and return result
    return Observable.merge(
      notificationSubscriptionObservable,
      rawDataPackageObservable
    )
  }
}