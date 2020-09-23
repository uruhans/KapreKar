package com.coloplast.olympus.datalayer.models


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 22/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class PhoneValidationRequestDTO(
  val phoneNumber: String,
  val securityCode: Int = -1
)