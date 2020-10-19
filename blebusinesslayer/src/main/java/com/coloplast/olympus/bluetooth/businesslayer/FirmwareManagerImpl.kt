package com.coloplast.olympus.bluetooth.businesslayer

import com.coloplast.olympus.bluetooth.businesslayer.firmware.CypressBootloaderModel
import com.coloplast.olympus.bluetooth.businesslayer.firmware.FirmwareUpdateModel
import com.coloplast.olympus.bluetooth.businesslayer.services.CommandService
import com.coloplast.olympus.bluetooth.businesslayer.services.OtaFirmware
import com.coloplast.olympus.businesslayer.interfaces.SystemUpdateComponent
import com.coloplast.olympus.shared.models.UpdateResult
import com.coloplast.olympus.shared.utils.FileUtils
import com.jakewharton.rxrelay2.PublishRelay
import com.polidea.rxandroidble2.RxBleConnection
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class FirmwareManagerImpl(
  private val bluetoothManager: BluetoothManager,
  private val systemUpdateComponent: SystemUpdateComponent
) : FirmwareManager {

  private val serviceRelay = PublishRelay.create<FirmwareManager.ServiceState>()

  override fun firmwareUpdateAvailable(macAddress: String, currentAppVersion: String): Observable<UpdateResult> {
    return systemUpdateComponent.firmwareUpdateAvailable(macAddress, currentAppVersion)
  }

  override fun initiateFirmwareDownload(macAddress: String, currentAppVersion: String) : Observable<FirmwareUpdateModel> {
    return systemUpdateComponent.initiateFirmwareDownload(macAddress, currentAppVersion)
      // this will save the incoming ByteArray to file system
      .map { data -> FileUtils.saveAsFileInExternalFilesDir(data) }
      // then unzip the zipped file into separate firmware and signature files
      .map { zippedFilename -> FileUtils.unzipFirmware(zippedFilename) }
      // and return a firmware update model (this uses the default naming conventions for firmwares and signatures)
      .map { FirmwareUpdateModel() }
  }

  override fun updateFirmware(macAddress: String, firmwareUpdateModel: FirmwareUpdateModel): Observable<Int> {
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
      .flatMap { connectBootloader(macAddress, firmwareUpdateModel) }
  }

  private fun connectBootloader(macAddress: String, firmwareUpdateModel: FirmwareUpdateModel): Observable<Int> {
    return bluetoothManager.connectOtaDevice(macAddress)
      // initiate firmware transfer
      .flatMap { initiateFirmwareTransfer(it, firmwareUpdateModel) }
      // upon complete firmware transfer (bootloader or app)
      // the transmitter will do a restart, meaning we will receive a disconnect
      // this retryWhen is here to catch that disconnect
      .retryWhen { errors ->
        errors.flatMap { error ->
          Timber.e(error)

          // if we aren't done with the firmware we are currently trying to update
          // we should retry connection and restart the firmware update
          if(firmwareUpdateModel.currentProgress() < 100) {
            // reset progress
            firmwareUpdateModel.resetProgress()
            // and reconnect to the bootloader
            Observable.timer(5, TimeUnit.SECONDS)
          }
          else {
            // check which state we are currently in and figure out
            // what to do next
            when (firmwareUpdateModel.getState()) {
              FirmwareUpdateModel.State.BOOTLOADER -> {
                // if we have just completed the bootloader firmware
                // switch state to app firmware, and wait 5 seconds to make
                // sure the transmitter has booted up completely
                firmwareUpdateModel.updateState(FirmwareUpdateModel.State.APP)
                // when this timer hits its onNext, the connectOtaDevice observable
                // will be re-subscribed
                Observable.timer(5, TimeUnit.SECONDS)
              }
              FirmwareUpdateModel.State.APP -> {
                // if we have just completed the app firmware
                firmwareUpdateModel.updateState(FirmwareUpdateModel.State.COMPLETED)
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
  }

  private fun initiateFirmwareTransfer(rxBleConnection: RxBleConnection, firmwareUpdateModel: FirmwareUpdateModel): Observable<Int> {
    return setupBootloaderResponseNotification(rxBleConnection)
      .flatMap { txResponsePacket ->
        // if the responsePacket from the transmitter is successful
        // send the next packet, and wait for next tx response
        if (txResponsePacket.isSuccessResponse()) {
          val commandPacket = firmwareUpdateModel.nextCommand()

          // write the next command
          writeNextCommand(rxBleConnection, commandPacket)
            // and return the current progress
            .toSingle { firmwareUpdateModel.totalProgress() }
            .toObservable()
            .doOnNext { Timber.d("Total Progress between the two firmwares: [$it]") }
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

  override fun serviceStateObservable(): Observable<FirmwareManager.ServiceState> {
    return serviceRelay
      .publish()
      .refCount()
  }
}