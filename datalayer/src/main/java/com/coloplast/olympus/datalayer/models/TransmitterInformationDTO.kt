package com.coloplast.olympus.datalayer.models

import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.transformers.OlympusWirelessProtocol
import com.squareup.moshi.Json
import java.util.*

class TransmitterInformationDTO(

  val id: String,
  val transmitterId: String,
  val userId: String,
  @Json(name = "firmwareRevision")
  val firmwareVersion: String?,
  val serialNumber: String?,
  val hardwareRevision: String?,
  @Json(name = "manufactureName")
  val manufactorerName: String?,
  @Json(name = "settingsPackage")
  val settings: SettingsDTO,
  @Json(name = "classifierPackage")
  val classifierSettings: ClassifierSettingsDTO
) {
  companion object {
    fun fromTransmitter(userId: UUID, transmitter: Transmitter): TransmitterInformationDTO {
      return TransmitterInformationDTO(
        UUID.randomUUID().toString(),
        transmitter.id,
        userId.toString(),
        transmitter.firmwareVersion,
        transmitter.serialNumber,
        transmitter.hardwareRevision,
        transmitter.manufacturerName,
        SettingsDTO.fromSettings(OlympusWirelessProtocol.settingsTransformer(transmitter.settings!!)),
        ClassifierSettingsDTO.fromClassifierSettings(
          OlympusWirelessProtocol.classifierSettingsTransformer(transmitter.classifierSettings!!)
        )
      )
    }
  }
}