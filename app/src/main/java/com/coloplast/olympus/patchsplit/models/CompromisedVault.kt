package com.coloplast.olympus.patchsplit.models

import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset

class CompromisedVault {

  private var running: Int = Int.MIN_VALUE
  private var timestamp: LocalDateTime = LocalDateTime.MIN

  fun set(compromised: Int, timestamp: LocalDateTime = LocalDateTime.now()) {
    if (compromised != -1 && compromised != 255 ) {
      Timber.d("Updated running compromised: [$compromised]")
      this.timestamp = timestamp
      running = compromised
    }
  }

  fun getRunningCompromised(): Int? {
    return if(running != Int.MIN_VALUE) running else null
  }

  fun getTimeSinceLast() : Long  {
    if(timestamp == LocalDateTime.MIN) return 0
    return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - timestamp.toEpochSecond(ZoneOffset.UTC)
  }
}