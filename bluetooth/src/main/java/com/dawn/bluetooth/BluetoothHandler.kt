package com.dawn.bluetooth

import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.polidea.rxandroidble2.*
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow


class BluetoothHandler(context: Context, isDebugging: Boolean) {
  
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
  }
  
  fun connectRxBleDevice(rxBleDevice: RxBleDevice): Observable<RxBleConnection> {
    return rxBleDevice.establishConnection(false)
      .flatMap { rxBleConnection ->
        rxBleDeviceConnectionStateObservable(rxBleDevice)
          .filter { it == RxBleConnection.RxBleConnectionState.CONNECTED }
          .flatMapSingle {
            Single.fromCallable { rxBleConnection.mtu }
              .flatMap { currentMtu ->
                Log.d("mtu", "currentMtu Size: $currentMtu")
                
                if(currentMtu < 185) {
                  rxBleConnection.requestMtu(185)
                }
                else {
                  Single.just(currentMtu)
                }
              }
          }
          .flatMapSingle {
            rxBleConnection.discoverServices()
              .map { rxBleDeviceServices ->
                rxBleDeviceServices.bluetoothGattServices.forEach { gattService ->
                  Log.d("Service", "[${gattService.uuid}]")
                  
                  gattService.characteristics.forEach {gattCharacteristics ->
                    Log.d("Characteristics", "  - ${gattCharacteristics.uuid}")
                  }
                }
              }
          }
          .map { rxBleConnection }
      }
  }
}