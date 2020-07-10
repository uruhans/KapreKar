package com.coloplast.olympus.ui.feature.onboarding.dataconsent

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.EmptyFlowFactoryModel
import com.coloplast.olympus.ui.common.flow.FlowFactory


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
    TODO("Not yet implemented")
  }


  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    //noop
  }

  override fun getData(): EmptyFlowFactoryModel = model
}