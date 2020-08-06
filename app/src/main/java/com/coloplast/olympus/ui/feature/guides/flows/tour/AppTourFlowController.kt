package com.coloplast.olympus.ui.feature.guides.flows.tour

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.common.models.flow.EmptyModel


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class AppTourFlowController: TopMenuFlowFactoryController<EmptyModel>() {

  private val model: FlowFactory<EmptyModel> by lazy { AppTourFactoryImpl() }

  override fun getFlowFactory(): FlowFactory<EmptyModel> = model

  companion object{
    private const val CONTROLLER_TAG = "app_tour_flow_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(AppTourFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}