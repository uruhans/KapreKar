package com.coloplast.olympus.datalayer.models

import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.transformers.OlympusWirelessProtocol
import java.time.Instant
import java.util.*

class TransmitterDTO(
  val userId: String,
  val id: String,
  val name: String,
  val deviceInfo: DeviceInfoDTO,
  val settings: SettingsDTO,
  val classifierSettings: ClassifierSettingsDTO
) {

  companion object {
    fun fromTransmitter(userId: UUID, transmitter: Transmitter): TransmitterDTO {
      return TransmitterDTO(
        userId.toString(),
        transmitter.id,
        transmitter.name,
        DeviceInfoDTO(
          Instant.now(),
          transmitter.manufacturerName,
          transmitter.hardwareRevision,
          transmitter.firmwareVersion,
          transmitter.serialNumber,
          transmitter.name
        ),
        SettingsDTO.fromSettings(OlympusWirelessProtocol.settingsTransformer(transmitter.settings!!)),
        ClassifierSettingsDTO.fromClassifierSettings(
          OlympusWirelessProtocol.classifierSettingsTransformer(
            transmitter.classifierSettings!!
          )
        )
      )
    }
  }

}