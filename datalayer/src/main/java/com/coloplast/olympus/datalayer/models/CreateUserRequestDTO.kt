package com.coloplast.olympus.datalayer.models

import com.squareup.moshi.JsonClass


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 09/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */

@JsonClass(generateAdapter = true)
data class CreateUserRequestDTO(
  val email: String,
  val password: String
)