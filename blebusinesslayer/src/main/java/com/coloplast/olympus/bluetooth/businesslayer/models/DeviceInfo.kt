package com.coloplast.olympus.bluetooth.businesslayer.models

class DeviceInfo(
  val macAddress: String,
  val manufacturer: String,
  val hardwareRevision: String,
  val firmwareVersion: String,
  val serialNumber: String
) {
  override fun toString(): String {
    return "manufacturer: $manufacturer \n" +
        "hardwareRevision: $hardwareRevision \n" +
        "firmwareVersion: $firmwareVersion \n" +
        "serialNumber: $serialNumber"
  }
}