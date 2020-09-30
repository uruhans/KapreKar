package com.coloplast.olympus.ui.feature.onboarding.signup

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.models.SU1Model
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.models.SU2Model
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.su1.CreateUserSU1Controller
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.su2.StomaInfoSU2Controller
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.su3.EmailSU3Controller
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.su4.PasswordSU4Controller
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.su5.PhoneSU5Controller
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.su6.PhoneValidationSU6
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.su7.InsuranceSU7Controller
import com.coloplast.olympus.ui.feature.onboarding.signup.createprofile.types.HealthInsurance
import com.coloplast.olympus.ui.feature.onboarding.signup.dataconsent.dc1.DataIntroDC1Controller
import com.coloplast.olympus.ui.feature.onboarding.signup.dataconsent.dc2.HelpYouDC2Controller
import com.coloplast.olympus.ui.feature.onboarding.signup.dataconsent.dc3.HelpCommunityDC3Controller
import com.coloplast.olympus.ui.feature.onboarding.signup.dataconsent.dc4.SafeWithUsDC4Controller
import com.coloplast.olympus.ui.feature.onboarding.signup.dataconsent.dc5.AcceptTermsDC5Controller
import com.coloplast.olympus.ui.feature.onboarding.termsofuse.TermsOfUseTUController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 29/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class SignupFactoryImpl : FlowFactory<SignupFactoryModel>() {

  private lateinit var model: SignupFactoryModel

  override fun initialize() {
    model = SignupFactoryModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when (SignupFlow.fromIndex(currentIndex)) {
      SignupFlow.TermsOfUseTU -> TermsOfUseTUController.routerTransaction()
      SignupFlow.DataConsentDC1 -> DataIntroDC1Controller.getRouterTransaction()
      SignupFlow.DataConsentDC2 -> HelpYouDC2Controller.getRouterTransaction()
      SignupFlow.DataConsentDC3 -> HelpCommunityDC3Controller.getRouterTransaction()
      SignupFlow.DataConsentDC4 -> SafeWithUsDC4Controller.getRouterTransaction()
      SignupFlow.DataConsentDC5 -> AcceptTermsDC5Controller.getRouterTransaction()
      SignupFlow.CreateUserSU1 -> CreateUserSU1Controller.getRouterTransaction()
      SignupFlow.StomaInfoSU2 -> StomaInfoSU2Controller.getRouterTransaction()
      SignupFlow.EmailSU3 -> EmailSU3Controller.getRouterTransaction()
      SignupFlow.PasswordSU4 -> PasswordSU4Controller.getRouterTransaction()
      SignupFlow.PhoneSU5 -> PhoneSU5Controller.getRouterTransaction()
      SignupFlow.PhoneValidationSU6 -> PhoneValidationSU6.getRouterTransaction()
      SignupFlow.InsuranceSU7 -> InsuranceSU7Controller.getRouterTransaction()
      SignupFlow.ConfirmationSU8 -> GenericConfirmationController.routerTransaction(
        R.string.onboarding_pc_title,
        R.string.onboarding_pc_description,
        R.string.onboarding_pc_button_text,
        R.drawable.ic_checkmark
      )
      SignupFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    @Suppress("NON_EXHAUSTIVE_WHEN")
    when (SignupFlow.fromIndex(currentIndex)) {
      SignupFlow.DataConsentDC1 -> {
        val skip = result as Boolean
        model.skip = skip
      }
      SignupFlow.DataConsentDC5 -> {
        model.user.consent = result as Boolean
      }
      SignupFlow.CreateUserSU1 -> {
        val data = result as SU1Model
        model.user.firstName = data.firstname
        model.user.lastName = data.lastname
      }
      SignupFlow.StomaInfoSU2 -> {
        val data = result as SU2Model
        model.user.stomaType = data.stomaType.id
        model.user.surgeryDateInterval = data.stomaSurgeryDate.id
      }
      SignupFlow.EmailSU3 -> {
        model.user.email = result as String
      }
      SignupFlow.PasswordSU4 -> {
        model.user.password = result as String
      }
      SignupFlow.PhoneSU5 -> {
        model.user.phoneNumber = result as String
      }
      SignupFlow.PhoneValidationSU6 -> {
        model.phoneValidationCode = result as String
      }
      SignupFlow.InsuranceSU7 -> {
        model.user.insuranceType = (result as HealthInsurance).id
      }
    }
  }

  override fun getData(): SignupFactoryModel = model

}
