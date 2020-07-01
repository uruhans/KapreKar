package com.coloplast.olympus.ui.feature.onboarding.createprofile

import com.coloplast.olympus.ui.common.flow.FlowFactoryModel
import java.time.Instant

class CreateUserModel: FlowFactoryModel {
  lateinit var firstName: String
  lateinit var lastName: String
  lateinit var stomaType: String
  lateinit var surgeryDate: Instant
  lateinit var email: String
  lateinit var password: String
  lateinit var address1: String
  var address2: String? = null
  lateinit var city: String
  lateinit var postCode: String
  lateinit var country: String
  lateinit var phoneNumber: String
  var healthInsurance: String? = null
}
