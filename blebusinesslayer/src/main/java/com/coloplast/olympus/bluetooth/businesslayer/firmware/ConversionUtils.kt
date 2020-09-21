package com.coloplast.olympus.bluetooth.businesslayer.firmware

fun parseHeader(line: String): CypressFirmware.Header {
  //val fileVersionLength = 2 //2 hex digits (1 byte)
  val siliconIdLength = 8 //4 bytes
  val siliconRevLength = 2 //1 byte
  val checkSumTypeLength = 2 //1 byte
  //val appIdLength = 2 //1 byte
  //val productIdLength = 8 //4 byte

  if (line.length == siliconIdLength + siliconRevLength + checkSumTypeLength) {
    var offset = 0
//    val fileVersion: Byte = hexStringToByteArrayLittleEndian(line, offset, fileVersionLength)!![0]
//    offset += fileVersionLength
    val siliconId: ByteArray = hexStringToByteArrayLittleEndian(line, offset, siliconIdLength)!!
    offset += siliconIdLength
    val siliconRev: Byte = hexStringToByteArrayLittleEndian(line, offset, siliconRevLength)!![0]
    offset += siliconRevLength
    val checkSumType: Byte = hexStringToByteArrayLittleEndian(line, offset, checkSumTypeLength)!![0]
    offset += checkSumTypeLength
//    val appId: Byte = hexStringToByteArrayLittleEndian(line, offset, appIdLength)!![0]
//    offset += appIdLength
//    val productId: ByteArray = hexStringToByteArrayLittleEndian(line, offset, productIdLength)!!
    return CypressFirmware.Header(siliconId, siliconRev, checkSumType)
  }
  throw IllegalArgumentException("Invalid Header line")
}

fun hexStringToByteArrayLittleEndian(string: String, offset: Int, length: Int): ByteArray? {
  return hexStringToByteArray(string, offset, length, length, true)
}

private fun hexStringToByteArray(string: String, aoffset: Int, length: Int, expectedLength: Int, isLittleEndian: Boolean): ByteArray? {
  var offset = aoffset
  val isOddNumChars = length % 2 == 1
  val numBytes = length / 2 + length % 2
  val expectedNumBytes = expectedLength / 2 + expectedLength % 2
  val maxNumBytes = Math.max(numBytes, expectedNumBytes)
  var bytes = ByteArray(maxNumBytes)
  var i = 0
  while (i < numBytes) {
    val idx: Int = if (isLittleEndian) {
      i
    }
    else {
      numBytes - 1 - i
    }
    val isHalfByte: Boolean = if (isLittleEndian) {
      i == numBytes - 1 && isOddNumChars
    }
    else {
      i == 0 && isOddNumChars
    }
    var b: Byte = hexStringToByte(string, offset, isHalfByte)
    if (isLittleEndian && isHalfByte) {
      b = (b.toInt() shl 4).toByte()
    }
    bytes[idx] = b
    if (isHalfByte) {
      offset++
    }
    else {
      offset += 2
    }
    i++
  }
  if (expectedNumBytes < numBytes) {
    val tmp = ByteArray(expectedNumBytes)
    System.arraycopy(bytes, 0, tmp, 0, tmp.size)
    bytes = tmp
  }
  return bytes
}

fun hexStringToByte(string: String, offset: Int, isHalfByte: Boolean): Byte {
  val s = string.substring(offset, offset + if (isHalfByte) 1 else 2)
  var value: Int = 0
  for (element in s) {
    value = (value shl 4)
    value += charToByte(element)
  }
  return value.toByte()
}

fun charToByte(value: Char): Byte {
  if (value in '0'..'9') return (value - '0').toByte()
  if (value in 'a'..'f') return (10 + value.toInt() - 'a'.toInt()).toByte()
  return if (value in 'A'..'F') (10 + value.toInt() - 'A'.toInt()).toByte() else 0
}

const val DISCRIMINATOR = ":"

fun parseData(line: String): CypressFirmware.DataRow {
  var offset: Int = DISCRIMINATOR.length
  val addressLength = 8 //4 bytes
  if (offset + addressLength <= line.length) { //Address is required, data is optional
    val address: ByteArray = hexStringToByteArrayLittleEndian(line, offset, addressLength)!!
    offset += addressLength
    val dataLength = line.length - offset //Remaining bytes for data
    val data: ByteArray = hexStringToByteArrayLittleEndian(line, offset, dataLength)!!
    return CypressFirmware.DataRow(address, data)
  }
  throw java.lang.IllegalArgumentException("Invalid Data line")
}