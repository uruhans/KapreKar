package com.coloplast.olympus.datalayer.models


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ChangePasswordRequest(
  val securityCode: Int = -1,
  val email: String,
  val newPassword: String = ""
)