package com.coloplast.olympus.ui.feature.onboarding.dataconsent

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 10/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class DataConsentFlowController: TopMenuFlowFactoryController<DataConsentModel>() {

  private val model by lazy { DataConsentFactoryImpl() }

  override fun getFlowFactory(): FlowFactory<DataConsentModel> = model

  override fun onStepCompleted(currentIndex: Int, data: DataConsentModel): Boolean {
    return when(DataConsentFlow.fromIndex(currentIndex)){
      DataConsentFlow.DataConsentDC1 -> {
        return if(data.skip){
          getFlowFactory().skipNext()
          getFlowFactory().skipNext()
          getFlowFactory().skipNext()
          getFlowFactory().incrementAndNext()
          true
        } else {
          false // go to next step as usual.
        }
      }
      /*CreateUserFlow.PhoneSU6 -> {
        if(data.phoneNumber.contains(UK_COUNTRY_CODE_1) || data.phoneNumber.contains(UK_COUNTRY_CODE_2)){
          getFlowFactory().skipNext()
          getFlowFactory().incrementAndNext()
          true
        }else {
          false // go to next step as usual.
        }
      }*/
      else -> super.onStepCompleted(currentIndex, data)
    }
  }

  companion object{
    private const val CONTROLLER_TAG = "data_consent_flow_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(DataConsentFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}