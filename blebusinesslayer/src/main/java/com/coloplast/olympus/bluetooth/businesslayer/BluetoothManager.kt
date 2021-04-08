package com.coloplast.olympus.bluetooth.businesslayer

import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.AdditionalDeviceInfo
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceInfo
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.shared.models.CompromisedPackage
import com.coloplast.olympus.shared.models.RawDataPackage
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

interface BluetoothManager {
  fun scanSpecificTransmitter(macAddress: String) : Observable<String>
  
  fun bluetoothStateObservable(): Observable<BluetoothState>
  fun initiateBleScanning(uuidFilter: UUID?): Observable<ScanResult>
  fun scanBleDevices(): Observable<ScanResult>
  fun connectBleDevice(macAddress: String): Observable<String>
  fun connectOtaDevice(macAddress: String): Observable<RxBleConnection>
  fun bleDisconnectedObservable(macAddress: String): Observable<BleConnectionState>
  fun bleConnectionStateObservable(macAddress: String): Observable<BleConnectionState>
  fun bleDeviceResultObservable(macAddress: String): Observable<BleDevice>
  fun setupOlympusRawDataPackageNotifications(macAddress: String): Observable<RawDataPackage>
  fun setupOlympusRunningCompromisedNotifications(macAddress: String) : Observable<CompromisedPackage>
  fun connectedDeviceObservable(macAddress: String): Observable<BleDevice>
  
  fun connectedDevicesObservable(): Observable<List<BleDevice>>
  fun stitchOrResetTransmitter(macAddress: String, data: ByteArray) : Completable
  fun writeCharacteristic(macAddress: String, characteristic: UUID, data: ByteArray) : Completable

  fun updateTransmitterTimestamp(macAddress: String) : Observable<String>
  fun readDeviceInfo(macAddress: String) : Observable<DeviceInfo>
  fun readAdditionalTransmitterInfo(macAddress: String) : Observable<AdditionalDeviceInfo>
  fun isConnected(macAddress: String) : Boolean

  fun disconnectTransmitter(macAddress: String)
  fun disconnectAllTransmitters()
}