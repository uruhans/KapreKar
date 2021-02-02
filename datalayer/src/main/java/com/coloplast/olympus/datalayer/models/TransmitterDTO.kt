package com.coloplast.olympus.datalayer.models

import com.coloplast.olympus.shared.models.Transmitter
import com.squareup.moshi.Json
import java.util.*

class TransmitterDTO(
  val id: String,
  val userId: String,
  val name: String,
  val nickName: String,
  @Json(name = "transmitterInformation")
  val transmitterInformationDTO: TransmitterInformationDTO
) {

  companion object {
    fun fromTransmitter(userId: UUID, transmitter: Transmitter): TransmitterDTO {
      return TransmitterDTO(
        UUID.randomUUID().toString(),
        userId.toString(),
        transmitter.name,
        transmitter.nickName,
        TransmitterInformationDTO.fromTransmitter(transmitter)
      )
    }
  }
}