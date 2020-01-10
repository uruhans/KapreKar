package com.coloplast.olympus.bluetooth.businesslayer

import android.content.Context
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.bluetooth.businesslayer.exceptions.DisconnectException
import com.coloplast.olympus.bluetooth.businesslayer.exceptions.GattCallbackTimeoutException
import com.coloplast.olympus.bluetooth.businesslayer.exceptions.GattException
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
import com.polidea.rxandroidble2.exceptions.BleDisconnectedException
import com.polidea.rxandroidble2.exceptions.BleException
import com.polidea.rxandroidble2.exceptions.BleGattCallbackTimeoutException
import com.polidea.rxandroidble2.exceptions.BleGattException
import io.reactivex.Observable
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.BiFunction
import io.reactivex.plugins.RxJavaPlugins

class BluetoothManagerImpl(context: Context, isDebugging: Boolean) : BluetoothManager {
  
  // a reference to the bluetoothHandler
  private val bluetoothHandler = BluetoothHandler(context, isDebugging)
  
  // a cached version of the bleDevices that we are currently connected to
  private val bleDevices = mutableMapOf<String, RxBleDevice>()
  // a cached version of the ble connections that we are currently connected to
  private val bleConnections = mutableMapOf<String, RxBleConnection>()
  
  init {
    /**
     * We set a custom error handler - first of all to convert rx library exceptions into something our app can handle
     * but also because there is an internal error in the library causing an Undeliverable Exception to be delivered multiple times
     * check here -> https://github.com/Polidea/RxAndroidBle/wiki/FAQ:-UndeliverableException
     */
    RxJavaPlugins.setErrorHandler {
      when {
        // ignore if undeliverable (others should have received it first) - Rx issue ->
        // https://github.com/Polidea/RxAndroidBle/wiki/FAQ:-UndeliverableException
        it is UndeliverableException && it.cause is BleException -> return@setErrorHandler
        it is BleDisconnectedException -> throw DisconnectException(it)
        it is BleGattCallbackTimeoutException -> throw GattCallbackTimeoutException(it)
        it is BleGattException -> throw GattException(it)
        else -> throw RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", it)
      }
    }
  }
  
  /**
   * An observable to listen to the current state of the BlueTooth radio, including PermissionStates and Location Services check
   * - output -> current bluetooth state (ready to SCAN/CONNECT when BluetoothState -> BluetoothState.READY)
   * - startWith -> BluetoothState.UNDEFINED
   */
  override fun bluetoothStateObservable(): Observable<BluetoothState> {
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
  
  /**
   * get a specific BleDevice based on it's MacAddress
   * if the device has already been connected too, it is stored locally and returned from here if possible, else we get it from the BluetoothHandler
   */
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
  
  /**
   * Initiate a scan of ble devices filtered by the Olympus Wireless Protocol UUID to limit results only to specific devices
   * - output -> BleDevice containing BleDevice Name, MacAddress and current ConnectionState
   */
  override fun scanBleDevices(): Observable<ScanResult> {
    return bluetoothHandler.scanBleDevices(OlympusWirelessProtocol.UUID)
      .map { ScanResult(it.bleDevice.name, it.bleDevice.macAddress, it.rssi) }
  }
  
  /**
   * gets the current state of a BleDevice
   * - output -> BleDevice containing the BleDevice name, macAddress and current ConnectionState
   */
  private fun rxBleDeviceConnectionState(macAddress: String): Observable<BleDevice> {
    return getBleDevice(macAddress)
      .flatMap { rxBleDevice ->
        bluetoothHandler.rxBleDeviceConnectionStateObservable(rxBleDevice)
          .map { BleDevice(rxBleDevice.name, rxBleDevice.macAddress, BleConnectionState.fromRxBleState(it)) }
      }
  }
  
  private fun bleDisconnectedObservable(macAddress: String): Observable<BleConnectionState> {
    return rxBleDeviceConnectionState(macAddress)
      .map { it.connectionState }
      .filter { it == BleConnectionState.DISCONNECTED || it == BleConnectionState.DISCONNECTING }
  }
  
  /**
   * connect to a specific ble device using it's MacAddress
   *
   * 1. we will wait until the connection state changes to CONNECTED
   * 2. we will request a change to the Mtu size, allowing for larger packages to be sent (it's upped from default 23 -> 64)
   * 3. then we discoverServices that exist on the connection - this is required to allow for READ, WRITE and NOTIFY on characteristics
   * 4. setup notifications (will cause an initial READ on each notification subscription)
   * 5. map result of notifications to a BleDevice containing Device Name, MacAddress, current ConnectionState and Peripheral state
   */
  override fun connectBleDevice(macAddress: String): Observable<BleDevice> {
    return getBleDevice(macAddress)
      .flatMap { rxBleDevice ->
        bluetoothHandler.connectRxBleDevice(rxBleDevice)
          .doOnNext { bleConnections[macAddress] = it }
          .flatMap { rxBleConnection ->
            rxBleDeviceConnectionState(macAddress)
              .filter { it.isConnected() }
              .flatMapSingle { rxBleConnection.requestMtu(64) }
              .flatMapSingle { rxBleConnection.discoverServices() }
              .flatMap { setupNotifications(rxBleConnection) }
              .map { BleDevice(rxBleDevice.name, rxBleDevice.macAddress, BleConnectionState.fromRxBleState(rxBleDevice.connectionState), it) }
          }
      }
  }
  
  /**
   * setup notifications to get notified when changes occur with the ble transmitter
   * - output -> Peripheral containing the PatchState and Transmitter state from the StatusVector and the current PowerLevel
   */
  private fun setupNotifications(rxBleConnection: RxBleConnection): Observable<Peripheral> {
    return Observable.combineLatest(
      notifyOlympusStatus(rxBleConnection),
      notifyBattery(rxBleConnection),
      BiFunction { statusVector: StatusVector, powerLevel: PowerLevel ->
        Peripheral(statusVector.patch, statusVector.transmitter, powerLevel)
      })
  }
  
  /**
   * setup notifications for the POWER characteristics on the BatteryPowerService
   *
   * - upon subscription this observable with do a READ CHARACTERISTIC in order to get the initial state of the characteristics
   * - output -> PowerLevel describing the current powerlevel of the transmitter (0-100% is converted into NEAR_EMPTY, LOW, MEDIUM and HIGH)
   */
  private fun notifyBattery(rxBleConnection: RxBleConnection): Observable<PowerLevel> {
    return rxBleConnection.setupNotification(BatteryPowerService.Characteristics.POWER)
      .flatMap { it }
      .startWith(rxBleConnection.readCharacteristic(BatteryPowerService.Characteristics.POWER).toObservable())
      .compose(BatteryPowerTransformer())
  }
  
  /**
   * setup notification for the STATUS VECTOR on the Olympus Wireless Protocol
   *
   * - upon subscription this observable with do a READ CHARACTERISTIC in order to get the initial state of the characteristics
   * - output -> StatusVector describing the current state of the patch and transmitter
   */
  private fun notifyOlympusStatus(rxBleConnection: RxBleConnection): Observable<StatusVector> {
    return rxBleConnection.setupNotification(OlympusWirelessProtocol.Characteristics.STATUS)
      .flatMap { it }
      .startWith(rxBleConnection.readCharacteristic(OlympusWirelessProtocol.Characteristics.STATUS).toObservable())
      .compose(OlympusStatusTransformer())
  }
  
  /**
   * setup notification for the RAW DATA PACKAGE on the Olympus Wireless Protocol
   *
   * - upon subscription this observable with do a READ CHARACTERISTIC in order to get the initial state of the characteristics
   * - output -> RawDataPackage containing the raw data state that the transmitter currently posses
   */
  private fun notifyOlympusRawDataPackage(rxBleConnection: RxBleConnection): Observable<RawDataPackage> {
    return rxBleConnection.setupNotification(OlympusWirelessProtocol.Characteristics.RAW_SENSOR_DATA)
      .flatMap { it }
      .startWith(rxBleConnection.readCharacteristic(OlympusWirelessProtocol.Characteristics.RAW_SENSOR_DATA).toObservable())
      .compose(OlympusRawDataPackageTransformer())
  }
}