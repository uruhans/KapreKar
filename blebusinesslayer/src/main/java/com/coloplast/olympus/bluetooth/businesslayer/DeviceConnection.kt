package com.coloplast.olympus.bluetooth.businesslayer

import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.models.DeviceResult
import com.coloplast.olympus.businesslayer.interfaces.SystemUpdateComponent
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.shared.models.UpdateResult
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class DeviceConnection(
  private val bluetoothManager: BluetoothManager,
  private val transmitterComponent: TransmitterComponent,
  private val systemUpdateComponent: SystemUpdateComponent
) {

  fun subscribeForNotifications(macAddress: String, currentAppVersion: String): Observable<DeviceResult> {

    // connect to specific device or use the connection we already have
    val connectedBleDeviceObservable = bluetoothManager.bleConnectionStateObservable(macAddress)
      .take(1)
      .delay(500, TimeUnit.MILLISECONDS)
      .flatMap { bleConnectionState ->
        if (bleConnectionState == BleConnectionState.DISCONNECTED) {
          // do actual connection to the new device
          bluetoothManager.connectBleDevice(macAddress)
            .flatMap { bluetoothManager.connectedDeviceObservable(macAddress) }
            .map { it }
        }
        else {
          // we are already connected, so grab that one
          bluetoothManager.connectedDeviceObservable(macAddress)
        }
      }
      // only continue if we are fully connected
      .filter { it.isConnected() }
      .flatMap {
        bluetoothManager.readAdditionalTransmitterInfo(macAddress)
          .flatMap { additionalDeviceInfo ->
            transmitterComponent.updateAdditionalInfoForTransmitter(
              macAddress,
              additionalDeviceInfo.deviceInfo.firmwareVersion,
              additionalDeviceInfo.deviceInfo.hardwareRevision,
              additionalDeviceInfo.deviceInfo.manufacturer,
              additionalDeviceInfo.deviceInfo.serialNumber,
              additionalDeviceInfo.settings,
              additionalDeviceInfo.classifierSettings
            )
              .toSingle { it }
              .toObservable()
          }
      }
      // i need multiple subscribers on this particular one
      .publish()
      .autoConnect()

    val systemUpdateObservable = systemUpdateComponent.systemUpdateAvailable(currentAppVersion)
      .cacheWithInitialCapacity(1)


    val notificationSubscriptionObservable = connectedBleDeviceObservable
      // this one subscribes to notifications and returns a peripheral object containing the latest state of the device
      .flatMap {
        bluetoothManager.bleDeviceResultObservable(macAddress)
          .retryWhen { it }
      }
      .map { DeviceResult.DeviceUpdateResult(it) }
      .flatMap { deviceUpdateResult ->
        systemUpdateObservable
          // map it to an actual device update
          .map { updateResult ->
            when (updateResult) {
              is UpdateResult.AppUpdateAvailable -> {
                DeviceResult.AppUpdateAvailable(updateResult, deviceUpdateResult.bleDevice)
              }
              is UpdateResult.FirmwareUpdateAvailable -> {
                DeviceResult.FirmwareAvailable(updateResult, deviceUpdateResult.bleDevice)
              }
              else -> deviceUpdateResult
            }
          }
      }

    val rawDataPackageObservable = connectedBleDeviceObservable
      // subscribe to raw data packages
      .flatMap {
        bluetoothManager.setupOlympusRawDataPackageNotifications(macAddress)
          .retryWhen { it }
      }
      // and map to a raw data result
      .map { DeviceResult.RawDataPackageResult(macAddress, it) }

    // finally merge and return result
    return Observable.merge(
      notificationSubscriptionObservable,
      rawDataPackageObservable
    )
  }
}