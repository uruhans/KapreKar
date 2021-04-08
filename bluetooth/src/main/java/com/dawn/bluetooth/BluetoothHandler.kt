package com.dawn.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.ParcelUuid
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
        .setShouldLogScannedPeripherals(true)
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

  fun connectRxBleDevice(rxBleDevice: RxBleDevice, serviceChangedUUID: UUID, mtuSize: Int = 185): Observable<RxBleConnection> {
    return BleDeviceBondingManager.createBond(context, rxBleDevice)
      .filter { rxBleDevice.bluetoothDevice.bondState == BluetoothDevice.BOND_BONDED }
      .flatMap { device ->
        device.establishConnection(false)
          // this delay is added in order to get the system to properly connect and update services properly (will not work without this delay)
          .delay(2, TimeUnit.SECONDS)
          .flatMap { rxBleConnection ->
            rxBleDeviceConnectionStateObservable(rxBleDevice)
              .filter { it == RxBleConnection.RxBleConnectionState.CONNECTED }
              .flatMapSingle { rxBleConnection.requestMtu(mtuSize) }
              .flatMapSingle { rxBleConnection.discoverServices() }
              // setup indication on the ServiceChanged characteristic
              .flatMap { rxBleConnection.setupIndication(serviceChangedUUID, NotificationSetupMode.DEFAULT) }
              .map { rxBleConnection }
          }
      }
  }
}