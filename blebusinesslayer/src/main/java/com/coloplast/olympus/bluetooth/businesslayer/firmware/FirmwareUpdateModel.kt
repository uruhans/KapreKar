package com.coloplast.olympus.bluetooth.businesslayer.firmware

import com.coloplast.olympus.shared.utils.FileUtils

class FirmwareUpdateModel(
  bootloaderName: String = "bootloader.cyacd",
  appFirmwareName: String = "firmware.cyacd",
  bootloaderSignatureName: String = "bootloader.signature",
  appSignatureName: String = "firmware.signature"
) {

  enum class State {
    BOOTLOADER,
    APP,
    COMPLETED,
  }

  private val bootloaderData = String(FileUtils.getFileFromExternalFilesAsByteArray(bootloaderName))
  private val appData = String(FileUtils.getFileFromExternalFilesAsByteArray(appFirmwareName))

  private val bootloaderSignatureAsString = String(FileUtils.getFileFromExternalFilesAsByteArray(bootloaderSignatureName))
  private val appSignatureAsString = String(FileUtils.getFileFromExternalFilesAsByteArray(appSignatureName))

  private val isBootloaderSignatureValid: Boolean
  private val isAppSignatureValid: Boolean

  private val bootloaderFirmware = getCypressFirmware(bootloaderData)
  private val appFirmware = getCypressFirmware(appData)

  private var currentState = State.BOOTLOADER
  private var currentFirmwareModel = CypressBootloaderModel(bootloaderFirmware)

  init {
    val publicKey = String(FileUtils.getFileFromAssetsAsByteArray("publickey.der"))

    isBootloaderSignatureValid = SignatureVerifier.verifySignature(bootloaderData, bootloaderSignatureAsString, publicKey)
    isAppSignatureValid = SignatureVerifier.verifySignature(appData, appSignatureAsString, publicKey)

    if(!signaturesValid()) {
      throw InvalidSignatureException("One or both signatures are invalid")
    }
  }

  private fun signaturesValid() : Boolean {
    return isBootloaderSignatureValid && isAppSignatureValid
  }

  fun nextCommand() : CypressBootloaderModel.CommandPacket {
    return currentFirmwareModel.nextCommandPacket()
  }

  fun currentProgress(): Int {
    return currentFirmwareModel.progress()
  }

  fun getState() : State {
    return currentState
  }

  fun updateState(newState: State) {
    currentState = newState

    if(currentState == State.APP) {
      currentFirmwareModel = CypressBootloaderModel(appFirmware)
    }
  }

  private fun getCypressFirmware(firmwareDataAsString: String): CypressFirmware {
    val lines = firmwareDataAsString.split("\r\n")

    val header = lines.first()
    val dataLines = lines.filter { it.isNotBlank() }.drop(1).map { readDataLine(it) }

    return CypressFirmware(readHeader(header), dataLines)
  }
}