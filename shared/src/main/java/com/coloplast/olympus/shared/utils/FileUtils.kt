package com.coloplast.olympus.shared.utils

import com.coloplast.olympus.shared.AppSettings
import java.io.ByteArrayOutputStream
import java.io.InputStream

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

}