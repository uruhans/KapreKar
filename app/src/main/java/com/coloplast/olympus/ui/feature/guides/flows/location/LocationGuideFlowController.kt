package com.coloplast.olympus.ui.feature.guides.flows.location

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 05/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class LocationGuideFlowController : TopMenuFlowFactoryController<LocationGuideEmptyModel>() {

  private val factory: LocationGuideFactoryImpl by inject()

  override fun getFlowFactory(): FlowFactory<LocationGuideEmptyModel> = factory

  companion object{
    private const val CONTROLLER_TAG = "location_guide_flow_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(LocationGuideFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}