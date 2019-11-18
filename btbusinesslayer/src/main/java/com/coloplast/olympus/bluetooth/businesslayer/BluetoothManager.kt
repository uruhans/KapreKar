package com.coloplast.olympus.bluetooth.businesslayer

import android.content.Context
import com.coloplast.olympus.bluetooth.businesslayer.characteristics.Characteristics
import com.coloplast.olympus.bluetooth.businesslayer.characteristics.Services
import com.coloplast.olympus.bluetooth.businesslayer.models.*
import com.dawn.bluetooth.BluetoothHandler
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.helpers.ValueInterpreter
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

class BluetoothManager(context: Context, isDebugging: Boolean) {
  
  private val bluetoothHandler = BluetoothHandler(context, isDebugging)
  
  private val bleDevices = mutableMapOf<String, RxBleDevice>()
  private val bleConnections = mutableMapOf<String, RxBleConnection>()
  
  fun bluetoothStateObservable(): Observable<BluetoothState> {
    return bluetoothHandler.rxBleClientStateObservable()
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
  
  private fun getBleDevice(macAddress: String): Observable<RxBleDevice> {
    return Observable.create { emitter ->
      if (bleDevices.any { it.key == macAddress }) {
        emitter.onNext(bleDevices[macAddress]!!)
      }
      else {
        val rxBleDevice = bluetoothHandler.getBleDevice(macAddress)
        bleDevices[macAddress] = rxBleDevice
        emitter.onNext(rxBleDevice)
      }
    }
  }
  
  fun scanBleDevices(): Observable<ScanResult> {
    return bluetoothHandler.scanBleDevices(Services.OLYMPUS_WIRELESS_PROTOCOL.asUUID())
        .map { ScanResult(it.bleDevice.name, it.bleDevice.macAddress, it.rssi) }
  }
  
  private fun rxBleDeviceConnectionState(macAddress: String): Observable<BleDevice> {
    return getBleDevice(macAddress)
        .flatMap { rxBleDevice ->
          bluetoothHandler.rxBleDeviceConnectionStateObservable(rxBleDevice)
              .map { state: RxBleConnection.RxBleConnectionState ->
                BleDevice(rxBleDevice.name, rxBleDevice.macAddress, BleConnectionState.fromRxBleState(state))
              }
        }
  }
  
  fun connectBleDevice(macAddress: String): Observable<BleDevice> {
    return getBleDevice(macAddress)
        .flatMap { rxBleDevice ->
          bluetoothHandler.connectRxBleDevice(rxBleDevice)
              .flatMap { rxBleConnection ->
                rxBleDeviceConnectionState(macAddress)
                    .filter { it.isFullyConnected() }
                    .flatMapSingle { rxBleConnection.discoverServices() }
                    .flatMap { addNotifications(rxBleConnection) }
                    .map { BleDevice(rxBleDevice.name, rxBleDevice.macAddress, BleConnectionState.fromRxBleState(rxBleDevice.connectionState), it) }
              }
        }
  }
  
  private fun addNotifications(rxBleConnection: RxBleConnection): Observable<Peripheral> {
    return Observable.combineLatest(
      notifyOlympusStatus(rxBleConnection),
      notifyBattery(rxBleConnection),
      BiFunction { olympusStatus: ByteArray, powerLevel: PowerLevel ->
        Peripheral(olympusStatus, powerLevel)
      })
  }
  
  private fun notifyBattery(rxBleConnection: RxBleConnection): Observable<PowerLevel> {
    return Observable.merge(
      rxBleConnection.setupNotification(Characteristics.POWER.asUUID()).flatMap { it },
      rxBleConnection.readCharacteristic(Characteristics.POWER.asUUID()).toObservable()
    )
        .map { ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT8, 0) }
        .map { PowerLevel.fromPercentage(it) }
  }
  
  
  private fun notifyOlympusStatus(rxBleConnection: RxBleConnection): Observable<ByteArray> {
    return Observable.merge(
      rxBleConnection.setupNotification(Characteristics.STATUS.asUUID()).flatMap { it },
      rxBleConnection.readCharacteristic(Characteristics.STATUS.asUUID()).toObservable()
    )
  }
}