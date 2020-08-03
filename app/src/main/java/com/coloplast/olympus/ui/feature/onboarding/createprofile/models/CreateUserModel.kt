package com.coloplast.olympus.ui.feature.onboarding.createprofile.models

import com.coloplast.olympus.ui.common.flow.FlowFactoryModel
import com.coloplast.olympus.ui.feature.onboarding.createprofile.types.HealthInsurance
import com.coloplast.olympus.ui.feature.onboarding.createprofile.types.StomaSurgeryDate
import com.coloplast.olympus.ui.feature.onboarding.createprofile.types.StomaType

class CreateUserModel: FlowFactoryModel {
  lateinit var firstName: String
  lateinit var lastName: String
  lateinit var stomaType: StomaType
  lateinit var stomaSurgeryDate: StomaSurgeryDate
  lateinit var email: String
  lateinit var password: String
  lateinit var phoneNumber: String
  var healthInsurance: HealthInsurance? = null
}
