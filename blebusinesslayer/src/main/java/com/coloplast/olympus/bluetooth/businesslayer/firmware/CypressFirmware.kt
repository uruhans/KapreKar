package com.coloplast.olympus.bluetooth.businesslayer.firmware

class CypressFirmware(
  val header: Header,
  val dataRows: List<DataRow>
) {

  class Header(
    val siliconId: String,
    val siliconRev: Int,
    checkSumType: String
  ) {
    val checkSum = Checksum.fromId(checkSumType.toInt())

    enum class Checksum(val id: Int) {
      checksum(0),
      crc16(1);

      companion object {
        fun fromId(value: Int): Checksum {
          return values().firstOrNull { it.id == value }
            ?: throw NullPointerException("Checksum with id=$value does not exist")
        }
      }
    }
  }

  class DataRow(
    val arrayId: Int,
    val rowNo: Int,
    val dataLength: Int,
    val data: ByteArray,
    val rowCheckSum: Int
  ) {

    override fun toString(): String {
      return "DataRow(arrayId=$arrayId, rowNo=$rowNo, dataLength=$dataLength, actualLength=${data.size}, rowChecksum=$rowCheckSum)"
    }
  }
}