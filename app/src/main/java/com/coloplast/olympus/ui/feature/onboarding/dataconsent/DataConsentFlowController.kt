package com.coloplast.olympus.ui.feature.onboarding.dataconsent

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.EmptyFlowFactoryModel
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 10/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class DataConsentFlowController: TopMenuFlowFactoryController<EmptyFlowFactoryModel>() {

  private val model by lazy { DataConsentFactoryImpl() }

  override fun getFlowFactory(): FlowFactory<EmptyFlowFactoryModel> = model

  companion object{
    private const val CONTROLLER_TAG = "data_consent_flow_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(DataConsentFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}