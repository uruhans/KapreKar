package com.coloplast.olympus.shared.models

import com.coloplast.olympus.shared.models.flow.FlowFactoryModel

class CreateUserModel:
  FlowFactoryModel {
  lateinit var firstName: String
  lateinit var lastName: String
  var stomaTypeId: Int = -1
  var stomaSurgeryDateId: Int = -1
  lateinit var email: String
  lateinit var password: String
  lateinit var phoneNumber: String
  var healthInsuranceId: Int = -1
}
