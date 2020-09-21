package com.dawn.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.polidea.rxandroidble2.*
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.Observable
import java.util.*
import java.util.concurrent.TimeUnit

class BluetoothHandler(private val context: Context, isDebugging: Boolean) {

  private val rxBleClient = RxBleClient.create(context)

  init {
    if (isDebugging) {
      val logOptions = LogOptions.Builder()
        .setLogLevel(LogConstants.DEBUG)
        .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
        .setUuidsLogSetting(LogConstants.UUIDS_FULL)
        .setShouldLogAttributeValues(true)
        .build()

      RxBleClient.updateLogOptions(logOptions)
    }
  }

  fun rxBleClientStateObservable(): Observable<RxBleClient.State> {
    return rxBleClient.observeStateChanges()
      .startWith(rxBleClient.state)
  }

  fun scanBleDevices(uuidFilter: UUID? = null): Observable<ScanResult> {
    val scanSettings = ScanSettings.Builder()
      .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
      .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
      .build()

    val scanFilter = ScanFilter.Builder()
    if (uuidFilter != null) {
      scanFilter.setServiceUuid(ParcelUuid(uuidFilter))
    }

    return rxBleClient.scanBleDevices(scanSettings, scanFilter.build())
  }

  fun getBleDevice(macAddress: String): RxBleDevice {
    return rxBleClient.getBleDevice(macAddress)
  }

  fun rxBleDeviceConnectionStateObservable(rxBleDevice: RxBleDevice): Observable<RxBleConnection.RxBleConnectionState> {
    return rxBleDevice
      .observeConnectionStateChanges()
      .startWith(rxBleDevice.connectionState)
      .distinctUntilChanged()
  }

  fun connectRxBleDevice(rxBleDevice: RxBleDevice, mtuSize: Int = 185): Observable<RxBleConnection> {
    return rxBleDevice.establishConnection(false)
      .flatMap { rxBleConnection ->
        rxBleDeviceConnectionStateObservable(rxBleDevice)
          .filter { it == RxBleConnection.RxBleConnectionState.CONNECTED }
          .flatMap {
            BleDeviceBondingManager.createBond(context, rxBleDevice)
              .filter { rxBleDevice.bluetoothDevice.bondState == BluetoothDevice.BOND_BONDED }
              .flatMapSingle { rxBleConnection.requestMtu(mtuSize) }
              .flatMap {
                rxBleConnection.discoverServices()
                  .toObservable()
                  .doOnNext { services ->
                    services.bluetoothGattServices.forEach { service ->
                      Log.d("BluetoothHandler", "service: ${service.uuid}")
                      service.characteristics.forEach { char ->
                        Log.d("BluetoothHandler", "char ${char.uuid}")
                      }
                    }
                  }
              }
              .map { rxBleConnection }
          }
      }
  }

  fun scanBeacons(uuidFilter: UUID?, macAddress: String? = null): Observable<String> {
    val scanSettings = ScanSettings.Builder()
      .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
      .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
      .build()

    val scanFilter = ScanFilter.Builder()
    if (uuidFilter != null) {
      scanFilter.setServiceUuid(ParcelUuid(uuidFilter))
    }

    macAddress?.let {
      scanFilter.setDeviceAddress(macAddress)
    }

    return rxBleClient.scanBleDevices(scanSettings, scanFilter.build())
      //.map { IBeacon.fromScanData(it.scanRecord.bytes, it.rssi, it.bleDevice.bluetoothDevice) }
      .map { it.bleDevice.macAddress }
  }
}