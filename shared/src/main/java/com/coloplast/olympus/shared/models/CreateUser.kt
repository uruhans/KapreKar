package com.coloplast.olympus.shared.models


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 17/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
open class CreateUser{
  lateinit var firstName: String
  lateinit var lastName: String
  var stomaTypeId: Int = -1
  var stomaSurgeryDateId: Int = -1
  lateinit var email: String
  lateinit var password: String
  lateinit var phoneNumber: String
  var healthInsuranceId: Int = -1
}