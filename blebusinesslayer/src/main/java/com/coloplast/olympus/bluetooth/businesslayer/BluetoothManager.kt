package com.coloplast.olympus.bluetooth.businesslayer

import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import io.reactivex.Observable

interface BluetoothManager {
  
  fun bluetoothStateObservable(): Observable<BluetoothState>
  fun scanBleDevices(): Observable<ScanResult>
  fun connectBleDevice(macAddress: String): Observable<BleDevice>
  
}