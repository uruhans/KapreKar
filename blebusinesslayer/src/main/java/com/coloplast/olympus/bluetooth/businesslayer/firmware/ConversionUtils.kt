package com.coloplast.olympus.bluetooth.businesslayer.firmware

import kotlin.experimental.and

fun readHeader(header: String): CypressFirmware.Header {
  val msbString: String = getMSB(header)
  val siliconId = msbString.substring(4, 12)
  val siliconRev = msbString.substring(2, 4)
  val checkSumType = msbString.substring(0, 2)

  return CypressFirmware.Header(siliconId, siliconRev.toInt(), checkSumType)
}

fun readDataLine(dataLine: String): CypressFirmware.DataRow {
  val sb = StringBuilder(dataLine)
  sb.deleteCharAt(0)
  val arrayId = sb.substring(0, 2).toInt(16)
  val rowNo = getMSB(sb.substring(2, 6))
  val dataLength = sb.substring(6, 10).toInt(16)
  val rowCheckSum = sb.substring(dataLine.length - 3, dataLine.length - 1).toInt(16)
  val dataString = sb.substring(10, dataLine.length - 2)

  val data = ByteArray(dataLength)
  var i = 0
  var j = 0

  while (i < dataLength) {
    data[i] = dataString.substring(j, j + 2).toInt(16).toByte()
    i++
    j += 2
  }

  val actualRowNo = "${rowNo.subSequence(2, 4)}${rowNo.subSequence(0, 2)}".toInt(16)

  return CypressFirmware.DataRow(arrayId, actualRowNo, dataLength, data, rowCheckSum)
}

fun getMSB(string: String): String {
  val msbString = StringBuilder()
  var i = string.length
  while (i > 0) {
    val str = string.substring(i - 2, i)
    msbString.append(str)
    i -= 2
  }
  return msbString.toString()
}

/**
 * Method to calculate 2's complement
 */
fun calculateCheckSum2(data: ByteArray): Int {
  var checkSum = 0

  data.forEach {
    checkSum += it.toUByte().toInt()
  }

  return 1 + checkSum.inv()
}