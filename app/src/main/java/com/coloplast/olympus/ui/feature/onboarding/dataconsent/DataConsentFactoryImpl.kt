package com.coloplast.olympus.ui.feature.onboarding.dataconsent

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc1.DataIntroDC1Controller
import com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc2.HelpYouDC2Controller
import com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc3.HelpCommunityDC3Controller
import com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc4.SafeWithUsDC4Controller
import com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc5.AcceptTermsDC5Controller
import com.coloplast.olympus.ui.feature.onboarding.termsofuse.TermsOfUseTUController

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 10/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class DataConsentFactoryImpl : FlowFactory<DataConsentModel>() {

  private lateinit var model: DataConsentModel

  override fun initialize() {
    model = DataConsentModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when (DataConsentFlow.fromIndex(currentIndex)) {
      DataConsentFlow.TermsOfUseTU -> TermsOfUseTUController.routerTransaction()
      DataConsentFlow.DataConsentDC1 -> DataIntroDC1Controller.getRouterTransaction()
      DataConsentFlow.DataConsentDC2 -> HelpYouDC2Controller.getRouterTransaction()
      DataConsentFlow.DataConsentDC3 -> HelpCommunityDC3Controller.getRouterTransaction()
      DataConsentFlow.DataConsentDC4 -> SafeWithUsDC4Controller.getRouterTransaction()
      DataConsentFlow.DataConsentDC5 -> AcceptTermsDC5Controller.getRouterTransaction()
      DataConsentFlow.ProfileComplete -> {
        GenericConfirmationController.routerTransaction(
          R.string.onboarding_pc_title,
          R.string.onboarding_pc_description,
          R.string.onboarding_pc_button_text,
          R.drawable.ic_checkmark
        )
      }
      else -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    @Suppress("NON_EXHAUSTIVE_WHEN")
    when (DataConsentFlow.fromIndex(currentIndex)) {
      DataConsentFlow.DataConsentDC1 -> {
        val skip = result as Boolean
        model.skip = skip
      }
      DataConsentFlow.DataConsentDC5 -> {
        model.didConsent = result as Boolean
      }
    }
  }

  override fun getData(): DataConsentModel = model
}