package com.coloplast.olympus.bluetooth.businesslayer

import android.content.Context
import com.coloplast.olympus.bluetooth.businesslayer.models.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.dawn.bluetooth.BluetoothHandler
import com.polidea.rxandroidble2.RxBleClient
import io.reactivex.Observable

class BluetoothManager(context: Context, isDebugging: Boolean) {
  
  private val bluetoothHandler = BluetoothHandler(context, isDebugging)
  
  fun bluetoothStateObservable(): Observable<BluetoothState> {
    return bluetoothHandler.bluetoothStateObservable()
        .map { state: RxBleClient.State ->
          when (state) {
            RxBleClient.State.BLUETOOTH_NOT_AVAILABLE -> {
              BluetoothState.BLUETOOTH_NOT_AVAILABLE
            }
            RxBleClient.State.LOCATION_PERMISSION_NOT_GRANTED -> {
              BluetoothState.LOCATION_PERMISSION_NOT_GRANTED
            }
            RxBleClient.State.BLUETOOTH_NOT_ENABLED -> {
              BluetoothState.BLUETOOTH_NOT_ENABLED
            }
            RxBleClient.State.LOCATION_SERVICES_NOT_ENABLED -> {
              BluetoothState.LOCATION_SERVICES_NOT_ENABLED
            }
            RxBleClient.State.READY -> {
              BluetoothState.READY
            }
          }
        }
        .startWith(BluetoothState.UNDEFINED)
  }
  
  fun scanBleDevices(): Observable<ScanResult> {
    return bluetoothHandler.scanBleDevices()
        .map { ScanResult(it.bleDevice.name, it.bleDevice.macAddress) }
  }
  
  fun connectBleDevice(macAddress: String) {

  }
}