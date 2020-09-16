package com.coloplast.olympus.databaselayer.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.shared.types.SyncState
import java.util.*

@Entity
class DbTransmitter(
  @PrimaryKey val id: String,
  @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
  val userId: UUID,
  val name: String,
  val nickName: String,
  val firmwareVersion: String?,
  val serialNumber: String?,
  val hardwareRevision: String?,
  val manufacturerName: String?,
  @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
  val settings: ByteArray?,
  @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
  val classifierSettings: ByteArray?,
  @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
  var syncState: SyncState
) {

  fun toTransmitter(): Transmitter {
    return Transmitter(
      id,
      userId,
      name,
      nickName,
      firmwareVersion,
      serialNumber,
      hardwareRevision,
      manufacturerName,
      settings,
      classifierSettings,
      syncState
    )
  }

  companion object {

    fun fromTransmitter(transmitter: Transmitter): DbTransmitter {
      return DbTransmitter(
        transmitter.id,
        transmitter.userId,
        transmitter.name,
        transmitter.nickName,
        transmitter.firmwareVersion,
        transmitter.serialNumber,
        transmitter.hardwareRevision,
        transmitter.manufacturerName,
        transmitter.settings,
        transmitter.classifierSettings,
        transmitter.syncState
      )
    }
  }
}