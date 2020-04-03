package com.coloplast.olympus.databaselayer.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coloplast.olympus.shared.models.Transmitter

@Entity
class DbTransmitter(
  @PrimaryKey val id: String,
  val customName: String
) {
  
  fun toTransmitter() : Transmitter {
    return Transmitter(
      id,
      customName
    )
  }
  
  companion object {
    
    fun fromTransmitter(transmitter: Transmitter) : DbTransmitter {
      return DbTransmitter(
        transmitter.macAddress,
        transmitter.customName
      )
    }
  }
}