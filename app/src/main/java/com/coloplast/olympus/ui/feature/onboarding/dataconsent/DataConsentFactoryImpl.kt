package com.coloplast.olympus.ui.feature.onboarding.dataconsent

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.EmptyFlowFactoryModel
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc1.DataIntroDC1Controller
import com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc2.HelpYouDC2Controller
import com.coloplast.olympus.ui.feature.onboarding.dataconsent.dc3.HelpCommunityDC3Controller


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 10/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class DataConsentFactoryImpl: FlowFactory<EmptyFlowFactoryModel>() {

  private lateinit var model: EmptyFlowFactoryModel

  override fun initialize() {
    model = EmptyFlowFactoryModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when(DataConsentFlow.fromIndex(currentIndex)){
      DataConsentFlow.DataConsentDC1 -> DataIntroDC1Controller.getRouterTransaction()
      DataConsentFlow.DataConsentDC2 -> HelpYouDC2Controller.getRouterTransaction()
      DataConsentFlow.DataConsentDC3 -> HelpCommunityDC3Controller.getRouterTransaction()
      else -> null
    }
  }


  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    //noop
  }

  override fun getData(): EmptyFlowFactoryModel = model
}