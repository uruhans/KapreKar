package com.coloplast.olympus.bluetooth.businesslayer.firmware

data class CypressFirmware(
  private val headerValue: String,
  private val dataValue: List<String>
) {
  val header = readHeader(headerValue)
  val dataRows = dataValue.map { readDataLine(it) }

  data class Header(
    val siliconId: String,
    val siliconRev: Int,
    private val checkSumType: String
  ) {
    val checkSum = Checksum.fromId(checkSumType.toInt())

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

  data class DataRow(
    val arrayId: Int,
    val rowNo: Int,
    val dataLength: Int,
    val data: ByteArray,
    val rowCheckSum: Int
    //val rowEnd: Boolean = false
  ) {

    override fun toString(): String {
      return "DataRow(arrayId=$arrayId, rowNo=$rowNo, dataLength=$dataLength, rowChecksum=$rowCheckSum)"
    }
  }
}