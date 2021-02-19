package com.coloplast.olympus.bluetooth.businesslayer

import android.content.Context
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.bluetooth.businesslayer.exceptions.DisconnectException
import com.coloplast.olympus.bluetooth.businesslayer.exceptions.GattCallbackTimeoutException
import com.coloplast.olympus.bluetooth.businesslayer.exceptions.GattException
import com.coloplast.olympus.bluetooth.businesslayer.models.*
import com.coloplast.olympus.bluetooth.businesslayer.services.*
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.CompromisedPackage
import com.coloplast.olympus.shared.models.RawDataPackage
import com.dawn.bluetooth.BluetoothHandler
import com.jakewharton.rxrelay2.PublishRelay
import com.microsoft.appcenter.crashes.Crashes
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.exceptions.BleDisconnectedException
import com.polidea.rxandroidble2.exceptions.BleException
import com.polidea.rxandroidble2.exceptions.BleGattCallbackTimeoutException
import com.polidea.rxandroidble2.exceptions.BleGattException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.BiFunction
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class BluetoothManagerImpl(
  private val transmitterComponent: TransmitterComponent,
  context: Context,
  isDebugging: Boolean
) : BluetoothManager {

  // a reference to the bluetoothHandler
  private val bluetoothHandler = BluetoothHandler(context, isDebugging)

  // a cached version of the bleDevices that we are currently connected to
  private val bleDevices = mutableMapOf<String, RxBleDevice>()

  // a cached version of the ble connections that we are currently connected to
  private val bleConnections = mutableMapOf<String, RxBleConnection>()
  private val currentPowerLevel = mutableMapOf<String, Int>()

  private val bleDeviceRelay = PublishRelay.create<List<BleDevice>>()
  private val bleConnectionRelay = PublishRelay.create<Map<String, RxBleConnection>>()

  private val disconnectRelay = PublishRelay.create<String>()

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
        it is UndeliverableException && it.cause is BleDisconnectedException -> throw DisconnectException(it)
        it is UndeliverableException && it.cause is BleGattCallbackTimeoutException -> throw GattCallbackTimeoutException(it)
        it is UndeliverableException && it.cause is BleGattException -> throw GattException(it)
        else -> {
          Crashes.trackError(it)
          return@setErrorHandler
        }
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

  override fun scanBeacons(): Observable<String> {
    return bluetoothHandler.scanBeacons(OlympusWirelessProtocol.UUID)
  }

  override fun scanSpecificBeacon(macAddress: String): Observable<String> {
    return bluetoothHandler.scanBeacons(OlympusWirelessProtocol.UUID, macAddress)
  }

  /**
   * get a specific BleDevice based on it's MacAddress
   * if the device has already been connected too, it is stored locally and returned from here if possible, else we get it from the BluetoothHandler
   */
  private fun getBleDevice(macAddress: String): Observable<RxBleDevice> {
    return Observable.create { emitter ->
      var rxBleDevice = bleDevices[macAddress]

      if (rxBleDevice == null) {
        rxBleDevice = bluetoothHandler.getBleDevice(macAddress)
        bleDevices[macAddress] = rxBleDevice
      }

      emitter.onNext(rxBleDevice)
    }
  }

  override fun initiateBleScanning(uuidFilter: UUID?): Observable<ScanResult> {
    return bluetoothHandler.scanBleDevices(uuidFilter)
      .map { ScanResult(it.bleDevice.name?.trim(), it.bleDevice.macAddress, it.rssi) }
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
          .doOnNext {
            removeDeviceFromConnectedList(it)
            removeConnectionFromList(it.macAddress)
          }
          .map { bleConnectionState }
      }
  }

  override fun bleConnectionStateObservable(macAddress: String): Observable<BleConnectionState> {
    return Observable.combineLatest(
      rxBleDeviceConnectionState(macAddress),
      bleConnectionObservable(),
      BiFunction { bleDevice: BleDevice, rxConnections: Map<String, RxBleConnection> ->
        if (bleDevice.connectionState == BleConnectionState.CONNECTED) {
          if (rxConnections.none { it.key == macAddress }) {
            return@BiFunction BleConnectionState.CONNECTING
          }
        }
        return@BiFunction bleDevice.connectionState
      })
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
      .switchMap { rxBleDevice ->
        bluetoothHandler.connectRxBleDevice(rxBleDevice, GenericAttribute.Characteristics.SERVICE_CHANGED)
          .takeUntil(
            disconnectRelay
              .publish()
              .refCount()
              .filter { it == macAddress }
              .map {
                removeConnectionFromList(macAddress)
                removeDeviceFromConnectedList(getBleDevice(macAddress).blockingFirst())
              }
          )
          .map {
            addDeviceToConnectedList(rxBleDevice)
            addConnectionToList(macAddress, it)
            macAddress
          }
      }
      .doOnError {
        removeConnectionFromList(macAddress)
        removeDeviceFromConnectedList(getBleDevice(macAddress).blockingFirst())
      }
  }

  override fun connectOtaDevice(macAddress: String): Observable<RxBleConnection> {
    return getBleDevice(macAddress)
      .switchMap { bluetoothHandler.connectRxBleDevice(it, GenericAttribute.Characteristics.SERVICE_CHANGED) }
  }

  /**
   * subscribe to the PeripheralObservable (which returns the current status of the transmitter)
   * - output -> a BleDevice containing name, macAddress, connectionState and Peripheral Status
   */
  override fun bleDeviceResultObservable(macAddress: String): Observable<BleDevice> {
    return peripheralObservable(macAddress)
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
  private fun peripheralObservable(macAddress: String): Observable<Peripheral> {
    return Observable.combineLatest(
      setupOlympusStatusVectorNotifications(macAddress),
      setupBatteryPowerNotifications(macAddress),
      { statusVector: StatusVector, powerLevel: PowerLevel ->
        var isBatteryUpdate: Boolean

        synchronized(currentPowerLevel) {
          isBatteryUpdate = if(currentPowerLevel[macAddress] == null) false else powerLevel.percentage != currentPowerLevel[macAddress]
        }

        Peripheral(statusVector.patch, statusVector.transmitter, powerLevel, isBatteryUpdate)
      })
      .doOnNext {
        synchronized(currentPowerLevel) {
          currentPowerLevel[macAddress] = it.battery.powerLevel.percentage
        }
      }
  }

  /**
   * setup notifications for the BATTERY POWER characteristics on the BatteryPowerService
   *
   * - upon subscription this observable with do a READ CHARACTERISTIC in order to get the initial state of the characteristics
   * - output -> PowerLevel describing the current powerlevel of the transmitter (0-100% is converted into NEAR_EMPTY, LOW, MEDIUM and HIGH)
   */
  private fun setupBatteryPowerNotifications(macAddress: String): Observable<PowerLevel> {
    val rxBleConnection = bleConnections[macAddress]!!

    return rxBleConnection.setupNotification(BatteryPowerService.Characteristics.POWER)
      .flatMap { it }
      .startWith(rxBleConnection.readCharacteristic(BatteryPowerService.Characteristics.POWER).toObservable())
      // store in local database
      .flatMapSingle { batteryPackage ->
        transmitterComponent.insertPowerPackage(macAddress, batteryPackage)
          .subscribeOn(Schedulers.io())
          .toSingle { batteryPackage }
          .onErrorReturn { batteryPackage }
      }
      .compose(BatteryPowerService.transformer)
  }

  /**
   * setup notifications for the STATUS VECTOR on the Olympus Wireless Protocol
   *
   * - upon subscription this observable with do a READ CHARACTERISTIC in order to get the initial state of the characteristics
   * - output -> StatusVector describing the current state of the patch and transmitter
   */
  private fun setupOlympusStatusVectorNotifications(macAddress: String): Observable<StatusVector> {
    val rxBleConnection = bleConnections[macAddress]!!

    return rxBleConnection.setupNotification(OlympusWirelessProtocol.Characteristics.STATUS)
      .flatMap { it }
      .startWith(rxBleConnection.readCharacteristic(OlympusWirelessProtocol.Characteristics.STATUS).toObservable())
      // store in local database
      .flatMapSingle { statusVector ->
        transmitterComponent.insertStatusPackage(macAddress, statusVector)
          .subscribeOn(Schedulers.io())
          .toSingle { statusVector }
          .onErrorReturn { statusVector }
      }
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
        // store in local database
        .flatMapSingle { rawPackage ->
          transmitterComponent.insertRawPackage(macAddress, rawPackage)
            .subscribeOn(Schedulers.io())
            .toSingle { rawPackage }
            .onErrorReturn { rawPackage }
        }
        .compose(OlympusWirelessProtocol.rawDataPackageTransformer)
    }
  }

  override fun setupOlympusRunningCompromisedNotifications(macAddress: String) : Observable<CompromisedPackage> {
    // fetch the ongoing connection based on the macAddress
    val rxBleConnection = bleConnections[macAddress]

    return if (rxBleConnection == null) {
      Observable.empty()
    }
    else {
      rxBleConnection.setupNotification(OlympusWirelessProtocol.Characteristics.COMPROMISED)
        .flatMap { it }
        .compose(OlympusWirelessProtocol.compromisedTransformer)
        .delay(200, TimeUnit.MILLISECONDS)
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

  private fun bleConnectionObservable(): Observable<Map<String, RxBleConnection>> {
    return bleConnectionRelay.publish().refCount().startWith(bleConnections)
  }

  private fun addConnectionToList(macAddress: String, rxBleConnection: RxBleConnection) {
    synchronized(bleConnections) {
      bleConnections[macAddress] = rxBleConnection
    }

    bleConnectionRelay.accept(bleConnections)
  }

  private fun removeConnectionFromList(macAddress: String) {
    synchronized(bleConnections) {
      bleConnections.remove(macAddress)
    }

    bleConnectionRelay.accept(bleConnections)
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

  override fun stitchOrResetTransmitter(macAddress: String, data: ByteArray): Completable {
    return writeCharacteristic(macAddress, OlympusWirelessProtocol.Characteristics.STATUS, data)
  }

  override fun updateTransmitterTimestamp(macAddress: String): Observable<String> {
    val currentTime = LocalDateTime.now()

    val bufferSize = 2
    val buffer = ByteBuffer.allocate(bufferSize)
    buffer.order(ByteOrder.LITTLE_ENDIAN) // transmitter is running little endian
    buffer.putShort(currentTime.year.toShort())

    val year = buffer.array()
    val month = currentTime.month.value.toByte()
    val day = currentTime.dayOfMonth.toByte()
    val hour = currentTime.hour.toByte()
    val minute = currentTime.minute.toByte()
    val second = currentTime.second.toByte()
    val dayOfWeek = (((currentTime.dayOfWeek.value + 5) % 7) + 1).toByte()
    val fraction: Byte = 0x00
    val reason: Byte = 0x02

    val timeStamp = byteArrayOf(year[0], year[1], month, day, hour, minute, second, dayOfWeek, fraction, reason)

    return writeCharacteristic(macAddress, CurrentTimeService.Characteristics.CURRENT_TIME, timeStamp)
      .toSingle { macAddress }
      .toObservable()
  }

  override fun readAdditionalTransmitterInfo(macAddress: String): Observable<AdditionalDeviceInfo> {
    return Observable.zip(
      readDeviceInfo(macAddress),
      readSettings(macAddress),
      readClassifierSettings(macAddress),
      { deviceInfo: DeviceInfo, settings: ByteArray, classifierSettings: ByteArray ->
        AdditionalDeviceInfo(deviceInfo, settings, classifierSettings)
      }
    )
  }

  override fun readDeviceInfo(macAddress: String): Observable<DeviceInfo> {
    return Observable.combineLatest(
      readCharacteristics(macAddress, DeviceInformation.Characteristics.MANUFACTURER_NAME).toObservable(),
      readCharacteristics(macAddress, DeviceInformation.Characteristics.HARDWARE_REVISION).toObservable(),
      readCharacteristics(macAddress, DeviceInformation.Characteristics.FIRMWARE_VERSION).toObservable(),
      readCharacteristics(macAddress, DeviceInformation.Characteristics.SERIAL_NUMBER).toObservable(),
      { manufacturer: ByteArray, hardwareRevision: ByteArray, firmwareVersion: ByteArray, serialNumber: ByteArray ->
        DeviceInfo(
          macAddress,
          manufacturer.toString(Charsets.UTF_8),
          hardwareRevision.toString(Charsets.UTF_8),
          firmwareVersion.toString(Charsets.UTF_8),
          serialNumber.toString(Charsets.UTF_8)
        )
      })
  }

  override fun isConnected(macAddress: String): Boolean {
    if (macAddress.isEmpty()) {
      return false
    }

    return bleConnections[macAddress] != null
  }

  override fun disconnectTransmitter(macAddress: String) {
    disconnectRelay.accept(macAddress)
  }

  override fun disconnectAllTransmitters() {
    bleDevices.keys.forEach { macAddress ->
      disconnectRelay.accept(macAddress)
    }
  }

  private fun readSettings(macAddress: String): Observable<ByteArray> {
    return readCharacteristics(macAddress, OlympusWirelessProtocol.Characteristics.SETTINGS)
      .toObservable()
  }

  private fun readClassifierSettings(macAddress: String): Observable<ByteArray> {
    return readCharacteristics(macAddress, OlympusWirelessProtocol.Characteristics.CLASSIFIER_SETTINGS)
      .toObservable()
  }

  private fun readCharacteristics(macAddress: String, characteristic: UUID): Single<ByteArray> {
    val rxConnection = bleConnections[macAddress]

    return if (rxConnection == null) {
      Single.error(NullPointerException("No connection exist to device with address: [$macAddress] - Unable to read characteristic: [$characteristic]"))
    }
    else {
      Timber.d("readCharacteristic: [$characteristic] on [$macAddress]")
      rxConnection.readCharacteristic(characteristic)
    }
  }

  override fun writeCharacteristic(macAddress: String, characteristic: UUID, data: ByteArray): Completable {
    val rxConnection = bleConnections[macAddress]

    return if (rxConnection == null) {
      Timber.e("writeCharacteristic: rxConnection was NULL")
      Completable.complete()
    }
    else {
      rxConnection.writeCharacteristic(characteristic, data)
        .ignoreElement()
    }
  }
}