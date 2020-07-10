package com.coloplast.olympus.datalayer.models

import com.coloplast.olympus.shared.models.User
import com.squareup.moshi.JsonClass
import java.time.Instant


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 09/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */

@JsonClass(generateAdapter = true)
data class UserDTO(
  val userId: Int,
  val firstName: String,
  val lastName: String,
  val email: String,
  val password: String,
  val role: Int,
  val countryId: Int,
  val registered: Instant,
  val isPasswordReset: Boolean
){

  fun toUser(): User{
    return User(
      success = true
    )
  }
}