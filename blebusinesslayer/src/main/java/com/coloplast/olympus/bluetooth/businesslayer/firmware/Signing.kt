package com.coloplast.olympus.bluetooth.businesslayer.firmware

import com.coloplast.olympus.shared.AppSettings.applicationContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

object Signing {

  fun verifySignature(dataToSign: ByteArray): Boolean {
    val publicKey = getAssetAsString("publickey.der")
    val signature = getAssetAsString("Firmware.signature")

    return SignatureVerifier.verifySignature(String(dataToSign), signature, publicKey)
  }

  private fun getAssetAsString(assetName: String): String {
    val inputStream: InputStream = applicationContext.assets.open(assetName)

    val buffer = ByteArray(8192)
    var bytesRead: Int
    val output = ByteArrayOutputStream()
    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
      output.write(buffer, 0, bytesRead)
    }

    return String(output.toByteArray())
  }
}
