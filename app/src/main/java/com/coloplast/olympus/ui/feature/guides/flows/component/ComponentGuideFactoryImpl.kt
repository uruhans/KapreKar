package com.coloplast.olympus.ui.feature.guides.flows.component

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.models.flow.EmptyModel
import com.coloplast.olympus.ui.feature.guides.flows.component.co10.ComponentGuideCO10Controller
import com.coloplast.olympus.ui.feature.guides.flows.component.co11.ComponentGuideCO11Controller
import com.coloplast.olympus.ui.feature.guides.flows.component.co12.ComponentGuideCO12Controller
import com.coloplast.olympus.ui.feature.guides.flows.component.co13.ComponentGuideCO13Controller
import com.coloplast.olympus.ui.feature.guides.flows.component.co2.ComponentGuideCO2Controller
import com.coloplast.olympus.ui.feature.guides.flows.component.co3.ComponentGuideCO3Controller
import com.coloplast.olympus.ui.feature.guides.flows.component.co4.ComponentGuideCO4Controller
import com.coloplast.olympus.ui.feature.guides.flows.component.co5.ComponentGuideCO5Controller
import com.coloplast.olympus.ui.feature.guides.flows.component.co6.ComponentGuideCO6Controller
import com.coloplast.olympus.ui.feature.guides.flows.component.co7.ComponentGuideCO7Controller
import com.coloplast.olympus.ui.feature.guides.flows.component.co8.ComponentGuideCO8Controller
import com.coloplast.olympus.ui.feature.guides.flows.component.co9.ComponentGuideCO9Controller


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ComponentGuideFactoryImpl : FlowFactory<EmptyModel>() {

  private lateinit var model: EmptyModel

  override fun initialize() {
    model = EmptyModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when (ComponentGuideFlow.fromId(currentIndex)) {
      ComponentGuideFlow.ComponentGuideCO1 -> InfoPageController.routerTransaction(
        iconId = R.drawable.ic_icon_guides,
        titleId = R.string.guide_co1_title,
        descriptionId = R.string.guide_co1_description,
        positiveButtonTextId = R.string.generic_continue,
        showCloseButton = true,
        showBackButton = true
      )
      ComponentGuideFlow.ComponentGuideCO2 -> ComponentGuideCO2Controller.routerTransaction()
      ComponentGuideFlow.ComponentGuideCO3 -> ComponentGuideCO3Controller.routerTransaction()
      ComponentGuideFlow.ComponentGuideCO4 -> ComponentGuideCO4Controller.routerTransaction()
      ComponentGuideFlow.ComponentGuideCO5 -> ComponentGuideCO5Controller.routerTransaction()
      ComponentGuideFlow.ComponentGuideCO6 -> ComponentGuideCO6Controller.routerTransaction()
      ComponentGuideFlow.ComponentGuideCO7 -> ComponentGuideCO7Controller.routerTransaction()
      ComponentGuideFlow.ComponentGuideCO8 -> ComponentGuideCO8Controller.routerTransaction()
      ComponentGuideFlow.ComponentGuideCO9 -> ComponentGuideCO9Controller.routerTransaction()
      ComponentGuideFlow.ComponentGuideCO10 -> ComponentGuideCO10Controller.routerTransaction()
      ComponentGuideFlow.ComponentGuideCO11 -> ComponentGuideCO11Controller.routerTransaction()
      ComponentGuideFlow.ComponentGuideCO12 -> ComponentGuideCO12Controller.routerTransaction()
      ComponentGuideFlow.ComponentGuideCO13 -> ComponentGuideCO13Controller.routerTransaction()
      ComponentGuideFlow.ComponentGuideCO14 -> GenericConfirmationController.routerTransaction(
        titleId = R.string.guide_co14_title,
        descriptionId = R.string.guide_co14_description,
        buttonId = R.string.generic_close
      )
      ComponentGuideFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    //noop
  }

  override fun getData(): EmptyModel = model
}