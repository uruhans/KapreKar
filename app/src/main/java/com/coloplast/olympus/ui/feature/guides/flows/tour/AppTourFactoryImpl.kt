package com.coloplast.olympus.ui.feature.guides.flows.tour

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.models.flow.EmptyModel
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap10.AppTourAP10Controller
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap11.AppTourAP11Controller
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap12.AppTourAP12Controller
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap2.AppTourAP2Controller
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap3.AppTourAP3Controller
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap4.AppTourAP4Controller
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap5.AppTourAP5Controller
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap6.AppTourAP6Controller
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap7.AppTourAP7Controller
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap8.AppTourAP8Controller
import com.coloplast.olympus.ui.feature.guides.flows.tour.ap9.AppTourAP9Controller


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
      AppTourFlow.AppTourAP5 -> AppTourAP5Controller.routerTransaction()
      AppTourFlow.AppTourAP6 -> AppTourAP6Controller.routerTransaction()
      AppTourFlow.AppTourAP7 -> AppTourAP7Controller.routerTransaction()
      AppTourFlow.AppTourAP8 -> AppTourAP8Controller.routerTransaction()
      AppTourFlow.AppTourAP9 -> AppTourAP9Controller.routerTransaction()
      AppTourFlow.AppTourAP10 -> AppTourAP10Controller.routerTransaction()
      AppTourFlow.AppTourAP11 -> AppTourAP11Controller.routerTransaction()
      AppTourFlow.AppTourAP12 -> AppTourAP12Controller.routerTransaction()
      AppTourFlow.AppTourAP13 -> GenericConfirmationController.getRouterTransaction(
        titleId = R.string.guide_ap13_title,
        descriptionId = R.string.guide_ap13_subtitle,
        buttonId = R.string.generic_close
      )
      else -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    //noop
  }

  override fun getData(): EmptyModel = model
}