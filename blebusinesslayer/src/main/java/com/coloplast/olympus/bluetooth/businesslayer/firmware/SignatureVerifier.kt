package com.coloplast.olympus.bluetooth.businesslayer.firmware

import android.util.Base64
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

object SignatureVerifier {
  private const val ENCRYPTION_ALGORITHM = "EC"
  private const val HASH_ENCRYPTION_ALGORITHM = "SHA256withECDSA"

  /**
   * Verify the signature for the given firmware using the DER format public key.
   *
   * @param firmwareAsString the firmware to verify signature against
   * @param signatureString the signature that we need to match in order to properly verify
   * @param publicKeyAsString the publickey in DER format to verify with
   * @return true if the signature verification succeeds, false otherwise.
   */
  fun verifySignature(firmwareAsString: String, signatureString: String, publicKeyAsString: String): Boolean {
    val firmwareBytes = firmwareAsString.toByteArray()
    val signatureBytes = Base64.decode(signatureString, Base64.NO_WRAP)
    val publicKey = getPublicKeyFromPemFile(publicKeyAsString)
    return verify(firmwareBytes, signatureBytes, publicKey)
  }

  /**
   * Verify the signature for the given signing message using the given public key.
   *
   * @param messageBytes the byte array of the signing message.
   * @param signatureBytes the byte array of the signature.
   * @param publicKey the public key.
   * @return true if the signature verification succeeds, false otherwise.
   */
  private fun verify(messageBytes: ByteArray, signatureBytes: ByteArray, publicKey: PublicKey): Boolean {
    val signature = Signature.getInstance(HASH_ENCRYPTION_ALGORITHM)
    signature.initVerify(publicKey)
    signature.update(messageBytes)
    return signature.verify(signatureBytes)
  }

  /**
   * Read the public key from given string.
   *
   * @param publicKeyAsString the publicKey (as DER) as string
   * @return the generated public key.
   */
  private fun getPublicKeyFromPemFile(publicKeyAsString: String): PublicKey {
    val keyFactory = KeyFactory.getInstance(ENCRYPTION_ALGORITHM)
    val publicKeyBase64Encoded = Base64.decode(publicKeyAsString, Base64.NO_WRAP)
    val publicKeySpec = X509EncodedKeySpec(publicKeyBase64Encoded)
    return keyFactory.generatePublic(publicKeySpec)
  }
}