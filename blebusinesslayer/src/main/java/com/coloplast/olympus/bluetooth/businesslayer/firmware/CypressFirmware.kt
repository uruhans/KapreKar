package com.coloplast.olympus.bluetooth.businesslayer.firmware

import com.coloplast.olympus.shared.transformers.ValueInterpreter
import timber.log.Timber

data class CypressFirmware(
  private val headerValue: String,
  private val dataValue: Array<String>
) {
  val header = Header(headerValue.toByteArray())
  val dataRows = dataValue.map { DataRow(it.toByteArray()) }

  init {
    Timber.d(headerValue)
    Timber.d(dataValue[0])
  }

  data class Header(private val headerValue: ByteArray) {
    val siliconId = ValueInterpreter.getIntValue(headerValue, ValueInterpreter.FORMAT_UINT32, 0).toULong()
    val siliconRev = headerValue[4].toInt()
    val checkSum = Checksum.fromId(headerValue[5].toInt())

    enum class Checksum(val id: Int) {
      checksum(0),
      crc16(1),
      UNKNOWN(3);

      companion object {
        fun fromId(value: Int): Checksum {
          return values().firstOrNull { it.id == value } ?: UNKNOWN
        }
      }
    }
  }

  data class DataRow(private val dataValue: ByteArray) {
    val arrayId = dataValue[0].toUByte()
    val rowNumber = ValueInterpreter.getIntValue(dataValue, ValueInterpreter.FORMAT_UINT16, 1).toUShort()
    val dataLength = ValueInterpreter.getIntValue(dataValue, ValueInterpreter.FORMAT_UINT16, 3).toUShort()
    //val data = dataValue.copyOfRange(5, dataLength.toInt() + 5)
    //val checkSum = ValueInterpreter.getIntValue(dataValue, ValueInterpreter.FORMAT_UINT16, data.size - 2).toUShort()
  }
}