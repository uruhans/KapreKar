package com.coloplast.olympus.bluetooth.businesslayer

import android.content.Context
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.bluetooth.businesslayer.models.*
import com.coloplast.olympus.bluetooth.businesslayer.services.BatteryPowerService
import com.coloplast.olympus.bluetooth.businesslayer.services.OlympusWirelessProtocol
import com.coloplast.olympus.bluetooth.businesslayer.transformers.BatteryPowerTransformer
import com.coloplast.olympus.bluetooth.businesslayer.transformers.OlympusRawDataPackageTransformer
import com.coloplast.olympus.bluetooth.businesslayer.transformers.OlympusStatusTransformer
import com.dawn.bluetooth.BluetoothHandler
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.internal.RxBleLog
import io.reactivex.Observable
import io.reactivex.functions.Function3
import timber.log.Timber

class BluetoothManager(context: Context, isDebugging: Boolean) {
  
  private val bluetoothHandler = BluetoothHandler(context, isDebugging)
  
  private val bleDevices = mutableMapOf<String, RxBleDevice>()
  private val bleConnections = mutableMapOf<String, RxBleConnection>()
  
  init {
    RxBleLog.setLogger { level: Int, tag: String?, msg: String? ->
      Timber.tag(tag)
          .log(level, msg)
    }
  }
  
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
    return bluetoothHandler.scanBleDevices(OlympusWirelessProtocol.UUID)
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
  
  @ExperimentalUnsignedTypes
  fun connectBleDevice(macAddress: String): Observable<BleDevice> {
    return getBleDevice(macAddress)
        .flatMap { rxBleDevice ->
          bluetoothHandler.connectRxBleDevice(rxBleDevice)
              .doOnNext { bleConnections[macAddress] = it }
              .flatMap { rxBleConnection ->
                rxBleDeviceConnectionState(macAddress)
                    .filter { it.isFullyConnected() }
                    .flatMapSingle { rxBleConnection.requestMtu(64) }
                    .flatMapSingle { rxBleConnection.discoverServices() }
                    .flatMap { setupNotifications(rxBleConnection) }
                    .map { BleDevice(rxBleDevice.name, rxBleDevice.macAddress, BleConnectionState.fromRxBleState(rxBleDevice.connectionState), it) }
              }
        }
  }
  
  private fun setupNotifications(rxBleConnection: RxBleConnection): Observable<Peripheral> {
    return Observable.combineLatest(
      notifyOlympusStatus(rxBleConnection),
      notifyBattery(rxBleConnection),
      notifyOlympusRawDataPackage(rxBleConnection),
      Function3 { statusVector: StatusVector, powerLevel: PowerLevel, rawDataPackage: RawDataPackage ->
        Peripheral(statusVector.patchState, statusVector.transmitterState, powerLevel)
      })
  }
  
  private fun notifyBattery(rxBleConnection: RxBleConnection): Observable<PowerLevel> {
    return rxBleConnection.setupNotification(BatteryPowerService.Characteristics.POWER)
        .flatMap { it }
        .startWith(rxBleConnection.readCharacteristic(BatteryPowerService.Characteristics.POWER).toObservable())
        .compose(BatteryPowerTransformer())
  }
  
  private fun notifyOlympusStatus(rxBleConnection: RxBleConnection): Observable<StatusVector> {
    return rxBleConnection.setupNotification(OlympusWirelessProtocol.Characteristics.STATUS)
        .flatMap { it }
        .startWith(rxBleConnection.readCharacteristic(OlympusWirelessProtocol.Characteristics.STATUS).toObservable())
        .compose(OlympusStatusTransformer())
  }
  
  private fun notifyOlympusRawDataPackage(rxBleConnection: RxBleConnection): Observable<RawDataPackage> {
    return rxBleConnection.setupNotification(OlympusWirelessProtocol.Characteristics.RAW_SENSOR_DATA)
        .flatMap { it }
        .startWith(rxBleConnection.readCharacteristic(OlympusWirelessProtocol.Characteristics.RAW_SENSOR_DATA).toObservable())
        .compose(OlympusRawDataPackageTransformer())
  }
}