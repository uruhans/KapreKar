package com.coloplast.olympus.bluetooth.businesslayer

import android.content.Context
import com.coloplast.olympus.bluetooth.businesslayer.firmware.CypressBootloaderModel
import com.coloplast.olympus.bluetooth.businesslayer.firmware.CypressFirmware
import com.coloplast.olympus.bluetooth.businesslayer.firmware.readDataLine
import com.coloplast.olympus.bluetooth.businesslayer.firmware.readHeader
import com.coloplast.olympus.bluetooth.businesslayer.services.CommandService
import com.coloplast.olympus.bluetooth.businesslayer.services.OtaFirmware
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

  override fun serviceCallback(): Observable<FirmwareManager.ServiceState> {
    return serviceRelay
      .publish()
      .refCount()
  }

  override fun updateFirmware(macAddress: String): Observable<Int> {
    // P command to this characteristic will put the transmitter into bootloader mode
    return bluetoothManager.writeCharacteristic(macAddress, CommandService.Characteristics.UART_RX, "P".toByteArray())
      .toSingle { true }
      .toObservable()
      .doOnNext {
        // the above writeCharacteristics will actually cause a disconnect,
        // but this disconnect will notify proper channels that it has happened
        bluetoothManager.disconnectTransmitter(macAddress)
        // send a STOP_SERVICE request to the service, as we dont want it to try to reconnect
        serviceRelay.accept(FirmwareManager.ServiceState.STOP_SERVICE)
      }
      // delay to make sure that we are completely disconnected
      .delay(2, TimeUnit.SECONDS)
      // connect to the bootloader
      .flatMap { connectBootloader(macAddress) }
  }

  private fun connectBootloader(macAddress: String): Observable<Int> {
    return bluetoothManager.connectOtaDevice(macAddress)
      // initiate firmware transfer
      .flatMap { initiateFirmwareTransfer(macAddress, it) }
      // upon complete firmware transfer (bootloader or app)
      // the transmitter will do a restart, meaning we will receive a disconnect
      // this retryWhen is here to catch that disconnect
      .retryWhen { errors ->
        errors.flatMap { error ->
          // TODO - probably check the error for a disconnect?
          Timber.e(error)

          // check which state we are currently in and figure out
          // what to do next
          when (currentState) {
            State.BOOTLOADER -> {
              // if we have just completed the bootloader firmware
              // switch state to app firmware, and wait 5 seconds to make
              // sure the transmitter has booted up completely
              currentState = State.APP
              // when this timer hits its onNext, the connectOtaDevice observable
              // will be re-subscribed
              Observable.timer(5, TimeUnit.SECONDS)
            }
            State.APP -> {
              // if we have just completed the app firmware
              // switch state to completed
              currentState = State.COMPLETED
              // send a START_SERVICE request to start the BluetoothConnectionService
              // to enable regular transmitter connection once again
              serviceRelay.accept(FirmwareManager.ServiceState.START_SERVICE)
              // return an error to stop the entire firmware upgrade flow
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
    // TODO - this needs to be redone - probably move it to a proper model
    val filename = when (currentState) {
      State.BOOTLOADER -> "OlympusBootloader_v2.1.6.cyacd"
      State.APP -> "OlympusApp_v2.1.3h.cyacd"
      else -> throw RuntimeException("FAILED")
    }

    val firmware = getCypressFirmware(filename)
    val model = CypressBootloaderModel(firmware)

    return setupBootloaderResponseNotification(rxBleConnection)
      .flatMap { txResponsePacket ->
        // if the responsePacket from the transmitter is successful
        // send the next packet, and wait for next tx response
        if (txResponsePacket.isSuccessResponse()) {
          val commandPacket = model.nextCommandPacket()

          // write the next command
          writeNextCommand(rxBleConnection, commandPacket)
            // and return the current progress
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
      // setup notifications on the OTA update characteristic
      // this will notify us when we've sent a command to the transmitter
      rxBleConnection.setupNotification(OtaFirmware.Characteristics.OTA_UPDATE)
        .flatMap { it }
        .map { CypressBootloaderModel.CommandPacket(it) },
      // because the setupNotification does not notify on successful subscription
      // we wait for a second and return one for ourselves to begin the firmware transfer
      Observable.timer(1, TimeUnit.SECONDS)
        .map { CypressBootloaderModel.CommandPacket(CypressBootloaderModel.CommandCode.Success) }
    )
  }

  private fun writeNextCommand(rxBleConnection: RxBleConnection, commandPacket: CypressBootloaderModel.CommandPacket): Completable {
    // write characteristics have no return value, so we ignore and complete it
    return rxBleConnection.writeCharacteristic(OtaFirmware.Characteristics.OTA_UPDATE, commandPacket.firmwareReadyData())
      .ignoreElement()
  }

  private fun getCypressFirmware(filename: String): CypressFirmware {
    val lines = String(FileUtils.fromAssets(filename)).split("\r\n")

    val header = lines.first()
    val dataLines = lines.filter { it.isNotBlank() }.drop(1).map { readDataLine(it) }

    return CypressFirmware(readHeader(header), dataLines)
  }
}