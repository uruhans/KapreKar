package com.coloplast.olympus.bluetooth.businesslayer

import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceInfo
import com.coloplast.olympus.bluetooth.businesslayer.models.RawDataPackage
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import io.reactivex.Completable
import io.reactivex.Observable

interface BluetoothManager {
  fun scanBeacons() : Observable<String>
  fun scanSpecificBeacon(macAddress: String) : Observable<String>
  
  fun bluetoothStateObservable(): Observable<BluetoothState>
  fun scanBleDevices(): Observable<ScanResult>
  fun connectBleDevice(macAddress: String): Observable<String>
  fun bleDisconnectedObservable(macAddress: String): Observable<BleConnectionState>
  fun bleConnectionStateObservable(macAddress: String): Observable<BleConnectionState>
  fun bleDeviceResultObservable(macAddress: String): Observable<BleDevice>
  fun setupOlympusRawDataPackageNotifications(macAddress: String): Observable<RawDataPackage>
  fun connectedDeviceObservable(macAddress: String): Observable<BleDevice>
  
  fun connectedDevicesObservable(): Observable<List<BleDevice>>
  fun resetTransmitter(macAddress: String) : Completable
  fun stitchTransmitter(macAddress: String, data: ByteArray) : Completable

  fun readDeviceInfo(macAddress: String) : Observable<DeviceInfo>
  fun isConnected(macAddress: String) : Boolean

  fun disconnectTransmitter(macAddress: String)
}