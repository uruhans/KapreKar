package com.coloplast.olympus.shared.models

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 17/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
open class CreateUserModel {
  var firstName = ""
  var lastName = ""
  var stomaTypeId: Int = -1
  var stomaSurgeryDateId: Int = -1
  var email = ""
  var password = ""
  var phoneNumber = ""
  var healthInsuranceId: Int = -1
}