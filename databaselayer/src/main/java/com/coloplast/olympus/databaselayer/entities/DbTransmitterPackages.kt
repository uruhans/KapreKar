package com.coloplast.olympus.databaselayer.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.coloplast.olympus.shared.types.PackageType
import com.coloplast.olympus.shared.types.SyncState
import java.time.Instant
import java.util.*

@Entity
class DbTransmitterPackages(
  @PrimaryKey val id: Int,
  val transmitterId: String,
  @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
  val deviceId: UUID,
  val patchId: Int,
  @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
  val timestamp: Instant,
  @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
  val packageType: PackageType,
  @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
  val value: ByteArray,
  @ColumnInfo(typeAffinity = ColumnInfo.INTEGER)
  val syncState: SyncState
)