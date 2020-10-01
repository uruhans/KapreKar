package com.coloplast.olympus.shared.utils

import com.coloplast.olympus.shared.AppSettings
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object FileUtils {

  fun fromAssets(assetName: String): ByteArray {
    val inputStream: InputStream = AppSettings.applicationContext.assets.open(assetName)

    val buffer = ByteArray(8192)
    var bytesRead: Int
    val output = ByteArrayOutputStream()
    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
      output.write(buffer, 0, bytesRead)
    }

    return output.toByteArray()
  }

  const val FIRMWARE_ZIP = "latest_firmware.zip"

  fun storeZippedFirmwareLocally(data: ByteArray) : String {
    val file = File(AppSettings.applicationContext.getExternalFilesDir(null), FIRMWARE_ZIP)

    if(file.exists()) {
      file.delete()
    }

    file.createNewFile()

    val fileOutputStream = FileOutputStream(file)
    fileOutputStream.write(data)
    fileOutputStream.close()

    return FIRMWARE_ZIP
  }

  data class ZipIO (val entry: ZipEntry, val output: File)

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
          Timber.d("zipped: $it")
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