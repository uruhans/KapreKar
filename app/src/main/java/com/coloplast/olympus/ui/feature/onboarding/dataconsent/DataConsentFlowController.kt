package com.coloplast.olympus.ui.feature.onboarding.dataconsent

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.shared.preferences.OnboardingProgression
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 10/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class DataConsentFlowController: TopMenuFlowFactoryController<DataConsentModel>() {

  private val factory by lazy { DataConsentFactoryImpl() }
  private val preferences: Preferences by inject()

  override fun getFlowFactory(): FlowFactory<DataConsentModel> = factory

  override fun onStepCompleted(currentIndex: Int, data: DataConsentModel): Boolean {
    return when(DataConsentFlow.fromIndex(currentIndex)){
      DataConsentFlow.DataConsentDC1 -> {
        return if(data.skip){
          with(factory){
            skipUntil(DataConsentFlow.DataConsentDC5.id)
            incrementAndNext()
          }
          true
        } else {
          false // go to next step as usual.
        }
      }
      DataConsentFlow.DataConsentDC5 -> {
        if(!data.didConsent) {
          activity?.finish()
          return true
        }
        false
      }
      else -> super.onStepCompleted(currentIndex, data)
    }
  }

  override fun onFlowCompleted(data: DataConsentModel) {
    preferences.setCurrentOnboardingProgression(OnboardingProgression.SETUP_GUIDE_INTRODUCTION)
    super.onFlowCompleted(data)
  }

  companion object{
    private const val CONTROLLER_TAG = "data_consent_flow_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(DataConsentFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}