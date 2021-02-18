package com.coloplast.olympus.bluetooth.businesslayer

import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset

class CompromisedVault {

  private var running: Int = Int.MIN_VALUE
  private var timestamp: LocalDateTime = LocalDateTime.MIN

  fun set(compromised: Int, timestamp: LocalDateTime = LocalDateTime.now()) {
    if (compromised != 255) {
      Timber.d("Setting new running compromised value: [$compromised]")
      this.timestamp = timestamp
      running = compromised
    }
  }

  fun get(): Int {
    return running
  }

  fun getIfValid(expirationTime: Long): Int? {
    if (timestamp.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) < expirationTime) {
      return running
    }

    return null
  }
}