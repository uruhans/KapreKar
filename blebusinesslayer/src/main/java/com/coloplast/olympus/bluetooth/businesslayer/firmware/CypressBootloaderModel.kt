package com.coloplast.olympus.bluetooth.businesslayer.firmware

import kotlin.math.min

@Suppress("LocalVariableName")
class CypressBootloaderModel(private val currentFirmware: CypressFirmware) {

  /// current data row
  private var currentRowDataArray: ByteArray? = null

//  /// Device flash start row number
//  private val startRowNumber: UShort = 0u
//
//  /// Device flash end row number
//  private val endRowNumber: UShort = 0u

  /// Max payload size for the data packages
  /// This is given as a max of the MTU packages size, set in the Transmitter! Which is 138 at the moment (21/9-2020)
  private val maxDataSize = 131

  /// current data row index
  private var currentIndex: Int = 0

  private var state = State.ENTER_BOOTLOADER

  enum class State {
    ENTER_BOOTLOADER,
    DATA,
    EXIT_BOOTLOADER
  }

  @Suppress("unused")
  enum class CommandCode(val code: Byte) {
    // Bootloader status/Error codes
    Success(0x00),
    FileError(0x01),
    EofError(0x02),
    LengthError(0x03),
    DataError(0x04),
    CommandError(0x05),
    DeviceError(0x06),
    VersionError(0x07),
    ChecksumError(0x08),
    ArrayError(0x09),
    RowError(0x0A),
    BootLoaderError(0x0B),
    ApplicationError(0x0C),
    ActiveError(0x0D),
    UnknownError(0x0F),
    AbortError(0xFF.toByte()),

    //Bootloader command codes
    VerifyChecksum(0x31),
    GetFlashSize(0x32),
    GetAppStatus(0x33),
    SetActiveApp(0x36),
    SendData(0x37),
    EnterBootLoader(0x38),
    ProgramRow(0x39),
    VerifyRow(0x3A),
    ExitBootLoader(0x3B)
  }

  class CommandPacket {
    val startByte: Byte = 0x01
    val command: Byte
    val data: ByteArray?
    val endByte: Byte = 0x17

    var arrayId: Int? = null
    var rowNo: Int? = null

    constructor(command: CommandCode, arrayId: Int? = null, rowNo: Int? = null, data: ByteArray? = null) {
      this.command = command.code
      this.data = data
      this.arrayId = arrayId
      this.rowNo = rowNo
    }

    constructor(command: CommandCode) {
      this.command = command.code
      this.data = null
    }

    constructor(txResponse: ByteArray) {
      this.command = txResponse[1]
      this.data = null
    }

    fun isSuccessResponse() : Boolean {
      return command == CommandCode.Success.code
    }

    fun firmwareReadyData(): ByteArray {
      val BASE_CMD_SIZE = 0x07 //SOP(1) + CmdCode(1) + DataLength(2) + Checksum(2) + EOP(1)

      var idx = 0
      val extraLength = if (command == CommandCode.ProgramRow.code) 3 else 0

      val dataLength = extraLength + (data?.size ?: 0)
      val commandSize: Int = BASE_CMD_SIZE + dataLength
      val commandBytes = ByteArray(commandSize)

      commandBytes[idx++] = startByte
      commandBytes[idx++] = command
      commandBytes[idx++] = dataLength.toByte()
      commandBytes[idx++] = (dataLength shr 8).toByte()

      if (command == CommandCode.ProgramRow.code) {
        commandBytes[idx++] = arrayId!!.toByte()
        commandBytes[idx++] = rowNo!!.toByte()
        commandBytes[idx++] = (rowNo!! shr 8).toByte()
      }

      data?.let {
        for (i in it.indices) {
          commandBytes[idx++] = it[i]
        }
      }

      val checkSum: Int = calculateCheckSum2(commandBytes)
      commandBytes[idx++] = checkSum.toByte()
      commandBytes[idx++] = (checkSum shr 8).toByte()
      commandBytes[idx++] = endByte

      //Timber.e("Command [$command] send size---> ${commandBytes.size}")
      return commandBytes
    }
  }

  fun progress() : Int {
    return min((currentIndex.toFloat() / currentFirmware.dataRows.size.toFloat() * 100f), 100f).toInt()
  }

  fun nextCommandPacket(): CommandPacket {
    if(state == State.ENTER_BOOTLOADER) {
      state = State.DATA
      return CommandPacket(CommandCode.EnterBootLoader)
    }
    else if(state == State.EXIT_BOOTLOADER) {
      return CommandPacket(CommandCode.ExitBootLoader)
    }

    if (currentIndex == currentFirmware.dataRows.size) {
      state = State.EXIT_BOOTLOADER
      return CommandPacket(CommandCode.VerifyChecksum)
    }

    val rowData = currentFirmware.dataRows[currentIndex]

    if (currentRowDataArray == null || currentRowDataArray!!.isEmpty()) {
      currentRowDataArray = rowData.data
    }

    return if (currentRowDataArray!!.size > maxDataSize) {
      // send SendData object
      val range = 0 until maxDataSize
      val dataToSend = currentRowDataArray!!.sliceArray(range)
      currentRowDataArray = currentRowDataArray!!.sliceArray(maxDataSize..currentRowDataArray!!.lastIndex)

      CommandPacket(CommandCode.SendData, rowData.arrayId, rowData.rowNo, dataToSend)
    }
    else {
      // send program row
      currentIndex++

      val dataToSend = currentRowDataArray
      currentRowDataArray = null
      CommandPacket(CommandCode.ProgramRow, rowData.arrayId, rowData.rowNo, dataToSend)
    }
  }
}