package com.coloplast.olympus.bluetooth.businesslayer.firmware

class CypressFirmware(
  headerValue: String,
  dataValue: Array<String>
) {
  val header = parseHeader(headerValue)
  val dataRows = dataValue.map { parseData(it) }

  class Header(val siliconId: ByteArray, val siliconRev: Byte, checkSumType: Byte) {
    val checkSum = Checksum.fromId(checkSumType.toInt())

    override fun toString(): String {
      return "siliconId: ${String(siliconId)}\r\nsiliconRev: $siliconRev\r\ncheckSum: $checkSum"
    }

    enum class Checksum(val id: Int) {
      checksum(0),
      crc16(1),
      UNKNOWN(2);

      companion object {
        fun fromId(value: Int): Checksum {
          return values().firstOrNull { it.id == value } ?: UNKNOWN
        }
      }
    }
  }

  class DataRow(val address: ByteArray, val data: ByteArray) {
    val dataLength = data.size

    override fun toString(): String {
      return "\r\nAddress: ${String(address)}\r\nLength: $dataLength\r\nData: ${String(data)}"
    }
  }
}