package com.coloplast.olympus.ui.common.extensions

import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import java.util.*

fun LocalDateTime.withDate(year: Int, month: Int, dayOfMonth: Int) : LocalDateTime {
  return this.also {
    it.withYear(year)
    it.withMonth(month)
    it.withDayOfMonth(dayOfMonth)
  }
}

fun LocalDateTime.toMillis() : Long {
  return this.toEpochSecond(ZoneOffset.UTC) * 1000
}

fun LocalDateTime.toDate() : Date {
  return Date(this.toMillis())
}

object LocalDateTimeHelper {
  fun ofMillis(millis: Long) : LocalDateTime {
    return LocalDateTime.ofEpochSecond(millis / 1000, 0, ZoneOffset.UTC)
  }
}