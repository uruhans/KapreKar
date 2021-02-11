package com.coloplast.olympus.aps

import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.AdditionalDeviceInfo
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceInfo
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.shared.models.RawDataPackage
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

class MockBluetoothManagerImpl : BluetoothManager {
  override fun scanBeacons(): Observable<String> {
    TODO("Not yet implemented")
  }

  override fun scanSpecificBeacon(macAddress: String): Observable<String> {
    TODO("Not yet implemented")
  }

  override fun bluetoothStateObservable(): Observable<BluetoothState> {
    TODO("Not yet implemented")
  }

  override fun initiateBleScanning(uuidFilter: UUID?): Observable<ScanResult> {
    TODO("Not yet implemented")
  }

  override fun scanBleDevices(): Observable<ScanResult> {
    TODO("Not yet implemented")
  }

  override fun connectBleDevice(macAddress: String): Observable<String> {
    TODO("Not yet implemented")
  }

  override fun connectOtaDevice(macAddress: String): Observable<com.polidea.rxandroidble2.RxBleConnection> {
    TODO("Not yet implemented")
  }

  override fun bleDisconnectedObservable(macAddress: String): Observable<BleConnectionState> {
    TODO("Not yet implemented")
  }

  override fun bleConnectionStateObservable(macAddress: String): Observable<BleConnectionState> {
    return Observable.just(BleConnectionState.CONNECTED)
  }

  override fun bleDeviceResultObservable(macAddress: String): Observable<BleDevice> {
    TODO("Not yet implemented")
  }

  override fun setupOlympusRawDataPackageNotifications(macAddress: String): Observable<RawDataPackage> {
    TODO("Not yet implemented")
  }

  override fun connectedDeviceObservable(macAddress: String): Observable<BleDevice> {
    TODO("Not yet implemented")
  }

  override fun connectedDevicesObservable(): Observable<List<BleDevice>> {
    TODO("Not yet implemented")
  }

  override fun stitchOrResetTransmitter(macAddress: String, data: ByteArray): Completable {
    TODO("Not yet implemented")
  }

  override fun writeCharacteristic(macAddress: String, characteristic: UUID, data: ByteArray): Completable {
    TODO("Not yet implemented")
  }

  override fun updateTransmitterTimestamp(macAddress: String): Observable<String> {
    TODO("Not yet implemented")
  }

  override fun readDeviceInfo(macAddress: String): Observable<DeviceInfo> {
    TODO("Not yet implemented")
  }

  override fun readAdditionalTransmitterInfo(macAddress: String): Observable<AdditionalDeviceInfo> {
    TODO("Not yet implemented")
  }

  override fun isConnected(macAddress: String): Boolean {
    return true
  }

  override fun disconnectTransmitter(macAddress: String) {
    TODO("Not yet implemented")
  }

  override fun disconnectAllTransmitters() {
    TODO("Not yet implemented")
  }
}