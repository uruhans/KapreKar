package com.coloplast.olympus.datalayer.models

import com.squareup.moshi.JsonClass
import java.time.Instant


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 09/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */

@JsonClass(generateAdapter = true)
data class CreateUserRequestDTO(
  val firstname: String,
  val lastname: String,
  val password: String,
  val email: String,
  val role: Int = 0,
  val countryId: Int = 0,
  val registered: Instant = Instant.now(),
  val isPasswordReset: Boolean = false

)