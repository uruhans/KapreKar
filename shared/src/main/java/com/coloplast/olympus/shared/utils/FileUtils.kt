package com.coloplast.olympus.shared.utils

import com.coloplast.olympus.shared.AppSettings
import timber.log.Timber
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object FileUtils {

  /**
   * Given an assetName will return the ByteArray
   *
   * @param assetName the AssetName to get
   */
  fun getFileFromAssetsAsByteArray(assetName: String): ByteArray {
    val inputStream: InputStream = AppSettings.applicationContext.assets.open(assetName)

    val buffer = ByteArray(8192)
    var bytesRead: Int
    val output = ByteArrayOutputStream()
    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
      output.write(buffer, 0, bytesRead)
    }

    return output.toByteArray()
  }

  /**
   * Returns a given file from the external files directory as a ByteArray
   *
   * @param filename The name of the file to get from
   */
  fun getFileFromExternalFilesAsByteArray(filename: String): ByteArray {
    val file = File("${AppSettings.applicationContext.getExternalFilesDir(null)}${File.separator}$filename")

    if(file.exists()) {
      return file.readBytes()
    }

    throw FileNotFoundException()
  }

  /**
   * Store a ByteArray of data in the external files directory, with a given filename
   *
   * @param data Data to store in the external files directory
   * @param filename Name of the file to store the data in
   */
  fun saveAsFileInExternalFilesDir(data: ByteArray, filename: String = "latest_firmware.zip") : String {
    val file = File(AppSettings.applicationContext.getExternalFilesDir(null), filename)

    if(file.exists()) {
      file.delete()
    }

    file.createNewFile()

    val fileOutputStream = FileOutputStream(file)
    fileOutputStream.write(data)
    fileOutputStream.close()

    return filename
  }

  /** ZipIO class to hold information about the current file in a zipped archive **/
  data class ZipIO (val entry: ZipEntry, val output: File)

  /**
   * Unzip a downloaded firmware archive, that contains the following
   *
   * - bootloader.cyacd (the bootloader firmware)
   * - bootloader.signature (the signature for the bootloader firmware)
   * - firmware.cyacd (the app firmware)
   * - firmware.signature (the signature for the app firmware)
   *
   * @param zippedFilename the name of the zippedFilename containing the firmwares to unzip
   */
  fun unzipFirmware(zippedFilename: String) : String {
    val unzipLocationRoot = AppSettings.applicationContext.getExternalFilesDir(null)

    val zipped = File(unzipLocationRoot, zippedFilename)
    if(!zipped.exists()) {
      throw RuntimeException("error")
    }

    ZipFile(zipped).use { zip ->
      zip
        .entries()
        .asSequence()
        .map {
          val outputFile = File(unzipLocationRoot!!.absolutePath + File.separator + it.name)
          val zipio = ZipIO(it, outputFile)
          zipio
        }
        .filter { it.entry.name.startsWith("_").not() }
        .map {
          it.output.parentFile?.run{
            if (!exists()) mkdirs()
          }
          it
        }
        .filter { !it.entry.isDirectory }
        .forEach { (entry, output) ->
          zip.getInputStream(entry).use { input ->
            output.outputStream().use { output ->
              input.copyTo(output)
            }
          }
        }
    }

    return zippedFilename
  }
}