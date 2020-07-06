package com.coloplast.olympus.bluetooth.businesslayer

import android.content.Context
import com.coloplast.olympus.bluetooth.businesslayer.extensions.asUUID
import com.coloplast.olympus.bluetooth.businesslayer.services.CommandService
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit
import kotlin.experimental.and


class FirmwareManagerImpl(
  private val applicationContext: Context,
  private val bluetoothManager: BluetoothManager
) : FirmwareManager {
  override fun checkFirmwareVersion(currentFirmwareVersion: String): Observable<Unit> {
    TODO("Not yet implemented")
  }
  val OTA_UPDATE_SERVICE = "00060001-f8ce-11e4-abf4-0002a5d5c51b".asUUID()

  override fun updateFirmware(macAddress: String): Completable {
    return bluetoothManager.writeCharacteristic(macAddress, CommandService.Characteristics.UART_RX, "P".toByteArray(Charsets.UTF_8))
      .toSingle { true }
      .toObservable()
      .doOnNext { Timber.d("FIRMWARE AFTER P COMMAND") }
      .flatMap {
        bluetoothManager.initiateBleScanning(null)
          .takeUntil(Observable.interval(5, TimeUnit.SECONDS))
          .filter { it.getNameOrAddress().toLowerCase().contains("ota") }
          .distinctUntilChanged()
          .buffer(5000, TimeUnit.SECONDS)
          .flatMapIterable { it }
          .take(1)
          .doOnNext { Timber.d("FIRMWARE Found OTA Bootloader") }
          .flatMap { scanResult ->
            bluetoothManager.connectOtaDevice(scanResult.macAddress)
              .delay(5, TimeUnit.SECONDS)
              .flatMap { rxBleConnection ->
                rxBleConnection.requestMtu(250)
                  .toObservable()
                  .map { rxBleConnection }
              }
              .flatMap { rxBleConnection ->
                rxBleConnection.discoverServices().toObservable()
                  .doOnNext {services ->
                    services.bluetoothGattServices.forEach { service ->
                      Timber.d("service: ${service.uuid}")
                      service.characteristics.forEach { char ->
                        Timber.e("char ${char.uuid}")
                      }
                    }
                  }
                  .map { rxBleConnection }
              }
              .doOnNext { Timber.d("Firmware OTA - connected") }
              .flatMap { rxBleConnection ->
                val fileData = getFileFromAssets()

                rxBleConnection.createNewLongWriteBuilder()
                  .setBytes(getData(fileData))
                  .setCharacteristicUuid(OTA_UPDATE_SERVICE)
                  .build()
              }
              .doOnError { throwable -> Timber.e(throwable) }
          }
          .doOnError { throwable -> Timber.e(throwable) }
      }
      .doOnError { throwable -> Timber.e(throwable) }
      .ignoreElements()
  }

  fun getFileFromAssets() : ByteArray {
    val inputStream: InputStream = applicationContext.assets.open("OlympusApp_v2.1.1m.cyacd")

    val buffer = ByteArray(8192)
    var bytesRead: Int
    val output = ByteArrayOutputStream()
    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
      output.write(buffer, 0, bytesRead)
    }

    return output.toByteArray()
  }

  val CRC_CHECKSUM = 0x01.toByte()
  val PACKET_START = 0x01
  val SEND_DATA = 0x37
  val PACKET_END = 0x17

  private fun getData(data: ByteArray) : ByteArray {
    var idx = 0
    val dataLength = data.size
    val commandSize = 7 + dataLength //BASE_CMD_SIZE
    val checksumableDataSize = commandSize - 3 //NON_CHECKSUMMABLE_SIZE

    val commandBytes = ByteArray(commandSize)
    commandBytes[idx++] = PACKET_START.toByte()
    commandBytes[idx++] = SEND_DATA.toByte()
    commandBytes[idx++] = dataLength.toByte()
    commandBytes[idx++] = dataLength.shr(8).toByte()

    data.forEach { byte ->
      commandBytes[idx++] = byte
    }

    val checksum = calculateCheckSum2(CRC_CHECKSUM, commandBytes, checksumableDataSize);
    commandBytes[idx++] = checksum.toByte() //Checksum
    commandBytes[idx++] = checksum.shr(8).toByte() //Checksum
    commandBytes[idx++] = PACKET_END.toByte() //End of packet

  //  val checksum =
    return commandBytes
  }



  fun calculateCheckSum2(checkSumType: Byte, data: ByteArray, dataSize: Int): Int {
    var dataLength = dataSize
    var checkSum = 0
    if (checkSumType == CRC_CHECKSUM) {
      return crc16(data, dataLength)
    }
    else {
      while (dataLength-- > 0) {
        checkSum += byteToIntUnsigned(data[dataLength])
      }
    }
    return 1 + checkSum.inv()
  }


  private fun crc16(data: ByteArray, dataLength: Int): Int {
    var crc = 0xffff
    var i: Int
    var tmp: Int
    if (dataLength == 0) {
      return crc.inv()
    }
    for (h in 0 until dataLength) {
      i = 0
      tmp = 0x00ff and data[h].toInt()
      while (i < 8) {
        crc = if (crc and 0x0001 xor (tmp and 0x0001) > 0) {
          crc shr 1 xor 0x8408
        }
        else {
          crc shr 1
        }
        i++
        tmp = tmp shr 1
      }
    }
    crc = crc.inv()
    tmp = crc
    crc = crc shl 8 or (tmp shr 8 and 0xFF)
    crc = 0xffff and crc
    return crc
  }

  fun byteToIntUnsigned(b: Byte): Byte {
    /**
     * AND with 0xFF to prevent returning negative value
     */
    return b and 0xFF.toByte()
  }

}



/*


    public void OTASendDataCmd(byte checkSumType, byte[] data) {
        int idx = 0;
        final int dataLength = data.length;
        final int commandSize = BootLoaderCommands_v1.BASE_CMD_SIZE + dataLength;
        final int checksumableDataSize = commandSize - NON_CHECKSUMMABLE_SIZE;

        byte[] commandBytes = new byte[commandSize];
        commandBytes[idx++] = (byte) BootLoaderCommands_v1.PACKET_START;//Start of packet
        commandBytes[idx++] = (byte) BootLoaderCommands_v1.SEND_DATA;//Command
        commandBytes[idx++] = (byte) dataLength;//Data length
        commandBytes[idx++] = (byte) (dataLength >> 8);//Data length
        //Data
        for (int i = 0; i < data.length; i++) {
            commandBytes[idx++] = data[i];
        }
        int checkSum = CheckSumUtils.calculateCheckSum2(ConvertUtils.byteToIntUnsigned(checkSumType), commandBytes, checksumableDataSize);
        commandBytes[idx++] = (byte) checkSum;//Checksum
        commandBytes[idx++] = (byte) (checkSum >> 8);//Checksum
        commandBytes[idx++] = (byte) BootLoaderCommands_v1.PACKET_END;//End of packet

        Logger.e("OTASendDataCmd send size--->" + commandBytes.length);
        BluetoothLeService.writeOTABootLoaderCommand(mOtaCharacteristic, commandBytes);
    }

 */