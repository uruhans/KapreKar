package com.coloplast.olympus.ui.feature.onboarding.createprofile

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.createprofile.models.CreateUserFactoryModel
import com.coloplast.olympus.ui.feature.onboarding.createprofile.models.SU1Model
import com.coloplast.olympus.ui.feature.onboarding.createprofile.models.SU2Model
import com.coloplast.olympus.ui.feature.onboarding.createprofile.su1.CreateUserSU1Controller
import com.coloplast.olympus.ui.feature.onboarding.createprofile.su2.StomaInfoSU2Controller
import com.coloplast.olympus.ui.feature.onboarding.createprofile.su3.EmailSU3Controller
import com.coloplast.olympus.ui.feature.onboarding.createprofile.su4.PasswordSU4Controller
import com.coloplast.olympus.ui.feature.onboarding.createprofile.su6.PhoneSU6Controller
import com.coloplast.olympus.ui.feature.onboarding.createprofile.su7.InsuranceSU7Controller
import com.coloplast.olympus.ui.feature.onboarding.createprofile.types.HealthInsurance

class CreateUserFactoryImpl : FlowFactory<CreateUserFactoryModel>() {

  private lateinit var model: CreateUserFactoryModel

  override fun initialize() {
    model = CreateUserFactoryModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when (CreateUserFlow.fromIndex(currentIndex)) {
      CreateUserFlow.CreateUserSU1 -> CreateUserSU1Controller.getRouterTransaction()
      CreateUserFlow.StomaInfoSU2 -> StomaInfoSU2Controller.getRouterTransaction()
      CreateUserFlow.EmailSU3 -> EmailSU3Controller.getRouterTransaction()
      CreateUserFlow.PasswordSU4 -> PasswordSU4Controller.getRouterTransaction()
      CreateUserFlow.PhoneSU6 -> PhoneSU6Controller.getRouterTransaction()
      CreateUserFlow.InsuranceSU7 -> InsuranceSU7Controller.getRouterTransaction()
      CreateUserFlow.ConfirmationSU8 -> GenericConfirmationController.routerTransaction(
        R.string.onboarding_confirmation_title,
        R.string.onboarding_confirmation_description,
        R.string.onboarding_confirmation_button
      )
      CreateUserFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    if (result == null) {
      return
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    when (CreateUserFlow.fromIndex(currentIndex)) {
      CreateUserFlow.CreateUserSU1 -> {
        val data = result as SU1Model
        model.user.firstName = data.firstname
        model.user.lastName = data.lastname
      }
      CreateUserFlow.StomaInfoSU2 -> {
        val data = result as SU2Model
        model.user.stomaType = data.stomaType.id
        model.user.surgeryDateInterval = data.stomaSurgeryDate.id
      }
      CreateUserFlow.EmailSU3 -> {
        model.user.email = result as String
      }
      CreateUserFlow.PasswordSU4 -> {
        model.user.password = result as String
      }
      CreateUserFlow.PhoneSU6 -> {
        model.user.phoneNumber = result as String
      }
      CreateUserFlow.InsuranceSU7 -> {
        model.user.insuranceType = (result as HealthInsurance).id
      }
    }
  }

  override fun getData(): CreateUserFactoryModel = model
}
