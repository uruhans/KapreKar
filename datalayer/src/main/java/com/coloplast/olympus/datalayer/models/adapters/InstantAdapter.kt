package com.coloplast.olympus.datalayer.models.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


/**
 * Μoshi adapter that deserializes a Date string into a JSR310 [Instant]
 */
@Suppress("unused")
class InstantAdapter {

  @FromJson
  fun fromJson(jsonReader: JsonReader): Instant? {

    if (jsonReader.peek() == JsonReader.Token.NULL) return jsonReader.nextNull()

    val dateString = jsonReader.nextString()
    if (dateString.isEmpty()) return null

    val localDateTime = LocalDateTime.parse(
      dateString,
      DateTimeFormatter.ofPattern(PATTERN)
    )
    return localDateTime.toInstant(ZoneOffset.UTC)
  }

  @ToJson
  fun toJson(date: Instant) = date.toString()

  companion object {
    private const val PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
  }
}