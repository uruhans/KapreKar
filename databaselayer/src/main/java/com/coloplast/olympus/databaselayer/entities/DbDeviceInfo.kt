package com.coloplast.olympus.databaselayer.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DbDeviceInfo(
  @PrimaryKey val macAddress: String,
  val manufacturerName: String,
  val hardwareRevision: String,
  val firmwareVersion: String,
  val serialNumber: String
)