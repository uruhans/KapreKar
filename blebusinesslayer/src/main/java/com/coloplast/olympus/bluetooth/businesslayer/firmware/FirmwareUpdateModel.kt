package com.coloplast.olympus.bluetooth.businesslayer.firmware

import com.coloplast.olympus.shared.utils.FileUtils

class FirmwareUpdateModel(
  bootloaderName: String,
  appFirmwareName: String,
  bootloaderSignatureName: String,
  appSignatureName: String
) {

  enum class State {
    BOOTLOADER,
    APP,
    COMPLETED,
  }

  private val bootloaderData = String(FileUtils.fromAssets(bootloaderName))
  private val appData = String(FileUtils.fromAssets(appFirmwareName))

  private val bootloaderSignatureAsString = String(FileUtils.fromAssets(bootloaderSignatureName))
  private val appSignatureAsString = String(FileUtils.fromAssets(appSignatureName))

  private val isBootloaderSignatureValid: Boolean
  private val isAppSignatureValid: Boolean

  private val bootloaderFirmware = getCypressFirmware(bootloaderData)
  private val appFirmware = getCypressFirmware(appData)

  private var currentState = State.BOOTLOADER
  private var currentFirmwareModel = CypressBootloaderModel(bootloaderFirmware)

  init {
    val publicKey = String(FileUtils.fromAssets("publickey.der"))

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

  fun firmwareCompleted() {
    when(currentState) {
      State.BOOTLOADER -> {
        currentState = State.APP
        currentFirmwareModel = CypressBootloaderModel(appFirmware)
      }
      State.APP -> {
        currentState = State.COMPLETED
      }
      State.COMPLETED -> {
        
      }
    }
  }

  private fun getCypressFirmware(firmwareDataAsString: String): CypressFirmware {
    val lines = firmwareDataAsString.split("\r\n")

    val header = lines.first()
    val dataLines = lines.filter { it.isNotBlank() }.drop(1).map { readDataLine(it) }

    return CypressFirmware(readHeader(header), dataLines)
  }
}