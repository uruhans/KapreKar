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
import com.dawn.bluetooth.BluetoothHandler
import com.jakewharton.rxrelay2.PublishRelay
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.exceptions.BleDisconnectedException
import com.polidea.rxandroidble2.exceptions.BleException
import com.polidea.rxandroidble2.exceptions.BleGattCallbackTimeoutException
import com.polidea.rxandroidble2.exceptions.BleGattException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.BiFunction
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import java.util.*

class BluetoothManagerImpl(context: Context, isDebugging: Boolean) : BluetoothManager {
  
  // a reference to the bluetoothHandler
  private val bluetoothHandler = BluetoothHandler(context, isDebugging)
  
  // a cached version of the bleDevices that we are currently connected to
  private val bleDevices = mutableMapOf<String, RxBleDevice>()
  
  // a cached version of the ble connections that we are currently connected to
  private val bleConnections = mutableMapOf<String, RxBleConnection>()
  
  private val bleDeviceRelay = PublishRelay.create<List<BleDevice>>()
  
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
        else -> return@setErrorHandler //throw RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", it)
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
  
  override fun scanBeacons() : Observable<String> {
    return bluetoothHandler.scanBeacons(OlympusWirelessProtocol.UUID)
  }
  
  override fun scanSpecificBeacon(macAddress: String) : Observable<String> {
    return bluetoothHandler.scanBeacons(OlympusWirelessProtocol.UUID, macAddress)
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
      // trimming the name to make sure we dont have whitespaces
      .map { ScanResult(it.bleDevice.name?.trim(), it.bleDevice.macAddress, it.rssi) }
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
  
  override fun bleDisconnectedObservable(macAddress: String): Observable<BleConnectionState> {
    return bleConnectionStateObservable(macAddress)
      .filter { it == BleConnectionState.DISCONNECTED || it == BleConnectionState.DISCONNECTING }
      .flatMap { bleConnectionState ->
        getBleDevice(macAddress)
          .doOnNext { removeDeviceFromConnectedList(it) }
          .map { bleConnectionState }
      }
  }
  
  override fun bleConnectionStateObservable(macAddress: String): Observable<BleConnectionState> {
    return rxBleDeviceConnectionState(macAddress)
      .map { it.connectionState }
      .publish()
      .refCount()
  }
  
  /**
   * connect to a specific ble device using it's macAddress
   *
   * 1. we will wait until the connection state changes to CONNECTED
   * 2. we will request a change to the Mtu size, allowing for larger packages to be sent (it's upped from default 23 -> 185)
   * 3. then we discoverServices that exist on the connection - this is required to allow for READ, WRITE and NOTIFY on characteristics
   * - output -> incoming macAddress
   */
  override fun connectBleDevice(macAddress: String): Observable<String> {
    return getBleDevice(macAddress)
      .flatMap { rxBleDevice ->
        bluetoothHandler.connectRxBleDevice(rxBleDevice)
          .doOnNext { addDeviceToConnectedList(rxBleDevice) }
          .map {
            bleConnections[macAddress] = it
            macAddress
          }
      }
  }
  
  /**
   * subscribe to the PeripheralObservable (which returns the current status of the transmitter)
   * - output -> a BleDevice containing name, macAddress, connectionState and Peripheral Status
   */
  override fun bleDeviceResultObservable(macAddress: String): Observable<BleDevice> {
    return peripheralObservable(bleConnections[macAddress]!!)
      .map {
        val rxBleDevice = bleDevices[macAddress]!!
        BleDevice(rxBleDevice.name, rxBleDevice.macAddress, BleConnectionState.fromRxBleState(rxBleDevice.connectionState), it)
      }
  }
  
  /**
   * setup notifications to get notified when changes occur with the ble transmitter
   * 1. setup notifications for StatusVector
   * 2. setup notifications for BatteryPower
   * - output -> a Peripheral object containing 1. and 2.
   *
   * note: even though only one of the the subscriptions are notifying we are returning the latest result from the others,
   * meaning we *always* get a full object containing the latest data sent from the transmitteræ
   */
  private fun peripheralObservable(rxBleConnection: RxBleConnection): Observable<Peripheral> {
    return Observable.combineLatest(
      setupOlympusStatusVectorNotifications(rxBleConnection),
      setupBatteryPowerNotifications(rxBleConnection),
      BiFunction { statusVector: StatusVector, powerLevel: PowerLevel ->
        Peripheral(statusVector.patch, statusVector.transmitter, powerLevel)
      })
      .distinctUntilChanged()
  }
  
  /**
   * setup notifications for the BATTERY POWER characteristics on the BatteryPowerService
   *
   * - upon subscription this observable with do a READ CHARACTERISTIC in order to get the initial state of the characteristics
   * - output -> PowerLevel describing the current powerlevel of the transmitter (0-100% is converted into NEAR_EMPTY, LOW, MEDIUM and HIGH)
   */
  private fun setupBatteryPowerNotifications(rxBleConnection: RxBleConnection): Observable<PowerLevel> {
    return rxBleConnection.setupNotification(BatteryPowerService.Characteristics.POWER)
      .flatMap { it }
      .startWith(rxBleConnection.readCharacteristic(BatteryPowerService.Characteristics.POWER).toObservable())
      .compose(BatteryPowerService.transformer)
  }
  
  /**
   * setup notifications for the STATUS VECTOR on the Olympus Wireless Protocol
   *
   * - upon subscription this observable with do a READ CHARACTERISTIC in order to get the initial state of the characteristics
   * - output -> StatusVector describing the current state of the patch and transmitter
   */
  private fun setupOlympusStatusVectorNotifications(rxBleConnection: RxBleConnection): Observable<StatusVector> {
    return rxBleConnection.setupNotification(OlympusWirelessProtocol.Characteristics.STATUS)
      .flatMap { it }
      .startWith(rxBleConnection.readCharacteristic(OlympusWirelessProtocol.Characteristics.STATUS).toObservable())
      .filter { statusVector -> statusVector.copyOfRange(0, 6).none { sensor -> sensor.toInt() == -1 } }
      .compose(OlympusWirelessProtocol.statusTransformer)
  }
  
  /**
   * setup notifications for the RawDataPackage on the Olympus Wireless Protocol (currently set to notify roughly every 10 seconds)
   *
   * - upon subscription this observable with do a READ CHARACTERISTIC in order to get the initial state of the characteristics
   * - output -> RawDataPackage describing the current transmitter state
   */
  override fun setupOlympusRawDataPackageNotifications(macAddress: String): Observable<RawDataPackage> {
    // fetch the ongoing connection based on the macAddress
    val rxBleConnection = bleConnections[macAddress]
    
    return if (rxBleConnection == null) {
      Observable.empty()
    }
    else {
      rxBleConnection.setupNotification(OlympusWirelessProtocol.Characteristics.RAW_SENSOR_DATA)
        .flatMap { it }
        .startWith(rxBleConnection.readCharacteristic(OlympusWirelessProtocol.Characteristics.RAW_SENSOR_DATA).toObservable())
        .compose(OlympusWirelessProtocol.rawDataPackageTransformer)
    }
  }
  
  override fun connectedDevicesObservable(): Observable<List<BleDevice>> {
    return bleDeviceRelay.publish()
      .refCount()
      // todo - this does not contain the latest peripheral - it probably should
      .startWith(bleDevices.map { BleDevice(it.value.name, it.value.macAddress, BleConnectionState.fromRxBleState(it.value.connectionState)) })
  }
  
  override fun connectedDeviceObservable(macAddress: String): Observable<BleDevice> {
    return getBleDevice(macAddress)
      .map { BleDevice(it.name, it.macAddress, BleConnectionState.fromRxBleState(it.connectionState)) }
  }
  
  private fun addDeviceToConnectedList(rxBleDevice: RxBleDevice) {
    synchronized(bleDevices) {
      bleDevices[rxBleDevice.macAddress] = rxBleDevice
    }
    notifyConnectedDevices()
  }

  private fun removeDeviceFromConnectedList(rxBleDevice: RxBleDevice) {
    synchronized(bleDevices) {
      bleDevices.remove(rxBleDevice.macAddress)
    }
    notifyConnectedDevices()
  }

  private fun notifyConnectedDevices() {
    bleDeviceRelay.accept(bleDevices.map {
      BleDevice(
        it.value.name,
        it.value.macAddress,
        BleConnectionState.fromRxBleState(it.value.connectionState)
      )
    })
  }

  override fun resetTransmitter(macAddress: String): Completable {
    return writeCharacteristic(macAddress, OlympusWirelessProtocol.Characteristics.STATUS, OlympusWirelessProtocol.Characteristics.RESET_STATUS_DATA)
  }

  override fun stitchTransmitter(macAddress: String, data: ByteArray): Completable {
    return writeCharacteristic(macAddress, OlympusWirelessProtocol.Characteristics.STATUS, data)
  }

  private fun writeCharacteristic(macAddress: String, characteristic: UUID, data: ByteArray) : Completable {
    val rxConnection = bleConnections[macAddress]

    return if(rxConnection == null) {
      Timber.e("writeCharacteristic: rxConnection was NULL")
      Completable.complete()
    }
    else {
      rxConnection.writeCharacteristic(characteristic, data)
        .ignoreElement()
    }
  }
}