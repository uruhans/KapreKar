package com.coloplast.olympus.shared.models

class StatusPackage(
  val sensor0: UByte,
  val sensor1: UByte,
  val sensor2: UByte,
  val sensor3: UByte,
  val sensor4: UByte,
  val sensor5: UByte,
  val wid: UByte,
  val attached: UByte,
  val patchType: UByte,
  val compromised: UByte,
  val angle: Int,
  val batteryState: UByte,
  val fwWarning: Int,
  val fwError: Int
)