package com.coloplast.olympus.bluetooth.businesslayer

import android.content.Context
import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID
import com.coloplast.olympus.bluetooth.businesslayer.firmware.CypressBootloaderModel
import com.coloplast.olympus.bluetooth.businesslayer.firmware.CypressFirmware
import com.coloplast.olympus.bluetooth.businesslayer.firmware.readDataLine
import com.coloplast.olympus.bluetooth.businesslayer.firmware.readHeader
import com.coloplast.olympus.bluetooth.businesslayer.services.CommandService
import com.coloplast.olympus.shared.utils.FileUtils
import com.jakewharton.rxrelay2.PublishRelay
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class FirmwareManagerImpl(
  private val applicationContext: Context,
  private val bluetoothManager: BluetoothManager
) : FirmwareManager {

  enum class State {
    BOOTLOADER,
    APP,
    COMPLETED,
  }

  private val serviceRelay = PublishRelay.create<FirmwareManager.ServiceState>()
  private var currentState = State.BOOTLOADER

  override fun checkFirmwareVersion(currentFirmwareVersion: String): Observable<Unit> {
    TODO("Not yet implemented")
  }

  private val OTA_UPDATE_SERVICE = "00060000-f8ce-11e4-abf4-0002a5d5c51b".asUUID()
  private val OTA_UPDATE_CHARACTERISTIC = "00060001-f8ce-11e4-abf4-0002a5d5c51b".asUUID()

  override fun serviceCallback(): Observable<FirmwareManager.ServiceState> {
    return serviceRelay
      .publish()
      .refCount()
  }

  override fun updateFirmware(macAddress: String): Observable<Int> {
    return bluetoothManager.writeCharacteristic(macAddress, CommandService.Characteristics.UART_RX, "P".toByteArray(Charsets.UTF_8))
      .toSingle { true }
      .toObservable()
      .doOnNext {
        bluetoothManager.disconnectTransmitter(macAddress)
        serviceRelay.accept(FirmwareManager.ServiceState.STOP_SERVICE)
      }
      .doOnNext { Timber.d("FIRMWARE AFTER P COMMAND") }
      .delay(2, TimeUnit.SECONDS)
      .flatMap { connectBootloader(macAddress) }
  }

  private fun connectBootloader(macAddress: String): Observable<Int> {
    return bluetoothManager.connectOtaDevice(macAddress)
      .flatMap { initiateFirmwareTransfer(macAddress, it) }
      .retryWhen { errors ->
        errors.flatMap { error ->
          Timber.e(error)
          when (currentState) {
            State.BOOTLOADER -> {
              currentState = State.APP
              Observable.timer(5, TimeUnit.SECONDS)
            }
            State.APP -> {
              currentState == State.COMPLETED
              serviceRelay.accept(FirmwareManager.ServiceState.START_SERVICE)
              Observable.error(error)
            }
            else -> {
              Observable.error(error)
            }
          }
        }
      }
  }

  private fun initiateFirmwareTransfer(macAddress: String, rxBleConnection: RxBleConnection): Observable<Int> {
    val filename = when (currentState) {
      State.BOOTLOADER -> "OlympusBootloader_v2.1.6.cyacd"
      State.APP -> "OlympusApp_v2.1.3h.cyacd"
      else -> throw RuntimeException("FAILED")
    }

    val firmware = getCypressFirmware(filename)
    val model = CypressBootloaderModel(firmware)

    return setupBootloaderResponseNotification(rxBleConnection)
      .flatMap { txResponsePacket ->
        if (txResponsePacket.isSuccessResponse()) {
          val commandPacket = model.nextCommandPacket()

          writeNextCommand(rxBleConnection, commandPacket)
            .toSingle { model.progress() }
            .toObservable()
            .doOnNext { Timber.d("Percentage: $it") }
        }
        else {
          Observable.error(RuntimeException("Its not working"))
        }
      }
  }

  private fun setupBootloaderResponseNotification(rxBleConnection: RxBleConnection): Observable<CypressBootloaderModel.CommandPacket> {
    return Observable.merge(
      rxBleConnection.setupNotification(OTA_UPDATE_CHARACTERISTIC)
        .flatMap { it }
        .map { CypressBootloaderModel.CommandPacket(it) },
      Observable.timer(1000, TimeUnit.MILLISECONDS)
        .map { CypressBootloaderModel.CommandPacket(CypressBootloaderModel.CommandCode.Success) }
    )
  }

  private fun writeNextCommand(rxBleConnection: RxBleConnection, commandPacket: CypressBootloaderModel.CommandPacket): Completable {
    return rxBleConnection.writeCharacteristic(OTA_UPDATE_CHARACTERISTIC, commandPacket.firmwareReadyData())
      .ignoreElement()
  }

  private fun getCypressFirmware(filename: String): CypressFirmware {
    val lines = String(FileUtils.fromAssets(filename)).split("\r\n")

    val header = lines.first()
    val dataLines = lines.filter { it.isNotBlank() }.drop(1).map { readDataLine(it) }

    return CypressFirmware(readHeader(header), dataLines)
  }
}