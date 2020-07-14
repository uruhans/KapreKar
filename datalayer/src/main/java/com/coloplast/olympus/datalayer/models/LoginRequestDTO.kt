package com.coloplast.olympus.datalayer.models


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 14/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
data class LoginRequestDTO(
  val email: String,
  val password: String
)