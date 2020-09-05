package com.coloplast.olympus.datalayer.models.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson
import java.time.Instant

class InstantAdapter {

  @FromJson
  fun fromJson(jsonReader: JsonReader): Instant? {
    if (jsonReader.peek() == JsonReader.Token.NULL) return jsonReader.nextNull()
    return Instant.ofEpochSecond(jsonReader.nextLong())
  }

  @ToJson
  fun toJson(date: Instant): Long {
    return date.toEpochMilli() / 1000
  }
}