package com.coloplast.olympus.databaselayer.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coloplast.olympus.shared.models.Transmitter

@Entity
class DbTransmitter(
  @PrimaryKey val serialNumber: String,
  val name: String,
  val userId: String? = null
) {
  
  fun toTransmitter(): Transmitter {
    return Transmitter(serialNumber, name)
  }
  
  companion object {
    
    fun fromTransmitter(transmitter: Transmitter): DbTransmitter {
      return DbTransmitter(
        transmitter.macAddress,
        transmitter.customName
      )
    }
  }
}