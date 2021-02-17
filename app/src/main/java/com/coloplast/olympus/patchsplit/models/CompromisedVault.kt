package com.coloplast.olympus.patchsplit.models

import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset

class CompromisedVault {

  private var running: Int = Int.MIN_VALUE
  private var timestamp: LocalDateTime = LocalDateTime.MIN

  fun set(compromised: Int) {
    if (compromised != 255) {
      Timber.d("Setting new running compromised value: [$compromised]")
      timestamp = LocalDateTime.now()
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