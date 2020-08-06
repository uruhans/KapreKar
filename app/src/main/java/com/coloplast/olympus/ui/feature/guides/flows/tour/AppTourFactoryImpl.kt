package com.coloplast.olympus.ui.feature.guides.flows.tour

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.models.flow.EmptyModel
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap2.AppTourAP2Controller
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap3.AppTourAP3Controller
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap4.AppTourAP4Controller


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class AppTourFactoryImpl: FlowFactory<EmptyModel>() {

  private lateinit var model: EmptyModel

  override fun initialize() {
    model = EmptyModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when(AppTourFlow.fromId(currentIndex)){
      AppTourFlow.AppTourAP1 -> InfoPageController.routerTransaction(
        iconId = R.drawable.ic_icon_guides,
        titleId = R.string.guide_ap1_title,
        descriptionId = R.string.guide_ap1_subtitle,
        positiveButtonTextId = R.string.generic_continue,
        showCloseButton = true,
        showBackButton = true
      )
      AppTourFlow.AppTourAP2 -> AppTourAP2Controller.routerTransaction()
      AppTourFlow.AppTourAP3 -> AppTourAP3Controller.routerTransaction()
      AppTourFlow.AppTourAP4 -> AppTourAP4Controller.routerTransaction()
      else -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    //noop
  }

  override fun getData(): EmptyModel = model
}