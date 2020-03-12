package com.coloplast.olympus.bluetooth.businesslayer

import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class DeviceConnection(private val bluetoothManager: BluetoothManager) {
  
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
            .map { bleConnectionState }
            .delay(3, TimeUnit.SECONDS)
        }
      }
      // continue to do so until the device gets disconnected
      // skip first because initial state is disconnected
      .takeUntil(
        bluetoothManager.bleDisconnectedObservable(macAddress)
          .skip(1)
      )
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