package com.coloplast.olympus.datalayer.models


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 09/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
data class CreateUserResponseDTO(
  val token: String,
  val userDto: UserDTO
)