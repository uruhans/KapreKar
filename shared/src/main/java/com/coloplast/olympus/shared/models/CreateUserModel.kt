package com.coloplast.olympus.shared.models

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
