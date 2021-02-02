package com.coloplast.olympus.datalayer.models

import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.transformers.OlympusWirelessProtocol
import com.squareup.moshi.Json
import java.util.*

class TransmitterInformationDTO(

  val id: String,
  val transmitterId: String,
  val firmwareVersion: String?,
  val serialNumber: String?,
  val hardwareRevision: String?,
  val manufactorerName: String?,
  @Json(name = "settings")
  val settings: SettingsDTO,
  @Json(name = "classifierSettings")
  val classifierSettings: ClassifierSettingsDTO
) {
  companion object {
    fun fromTransmitter(transmitter: Transmitter): TransmitterInformationDTO {
      return TransmitterInformationDTO(
        UUID.randomUUID().toString(),
        transmitter.id,
        transmitter.firmwareVersion,
        transmitter.serialNumber,
        transmitter.hardwareRevision,
        transmitter.manufacturerName,
        SettingsDTO.fromSettings(OlympusWirelessProtocol.settingsTransformer(transmitter.settings!!)),
        ClassifierSettingsDTO.fromClassifierSettings(
          OlympusWirelessProtocol.classifierSettingsTransformer(
            transmitter.classifierSettings!!
          ))
      )
    }
  }
}