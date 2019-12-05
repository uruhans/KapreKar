package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class SensorStatus(val id: Int) {
  OK(0),
  BREACH(1),
  SOME_SWEAT(2),
  BLINDED(3),
  PAY_ATTENTION(4),
  NOISE(5),
  
  CUT_GUARD_OK(17),
  CUT_GUARD_CUT_BROKEN(18),
  CUT_GUARD_DIRT_IN_CONNECTOR(19),
  CUT_GUARD_NOISE(20),
  UNKNOWN(-1);
  
  companion object {
    fun fromId(id: UByte): SensorStatus {
      return values().associateBy { sensorStatus: SensorStatus -> sensorStatus.id }[id.toInt()]
        ?: UNKNOWN
    }
  }
}