package com.coloplast.olympus.ui.feature.guides.flows.location

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.guides.flows.location.lg2.*


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 05/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class LocationGuideFactoryImpl: FlowFactory<LocationGuideEmptyModel>(){

  private lateinit var model: LocationGuideEmptyModel

  override fun initialize() {
    model = LocationGuideEmptyModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? =
    when(LocationGuideFlow.fromId(currentIndex)){
      LocationGuideFlow.LocationGuideLG1 -> InfoPageController.routerTransaction(
        iconId = R.drawable.ic_icon_guides,
        titleId = R.string.guide_lg1_title,
        descriptionId = R.string.guide_lg1_subtitle,
        positiveButtonTextId = R.string.generic_continue,
        showCloseButton = true,
        showBackButton = true
      )
      LocationGuideFlow.LocationGuideLG2 -> LocationGuideLG2Controller.routerTransaction()
      LocationGuideFlow.LocationGuideLG3 -> LocationGuideLG3Controller.routerTransaction()
      LocationGuideFlow.LocationGuideLG4 -> LocationGuideLG4Controller.routerTransaction()
      LocationGuideFlow.LocationGuideLG5 -> LocationGuideLG5Controller.routerTransaction()
      LocationGuideFlow.LocationGuideLG6 -> LocationGuideLG6Controller.routerTransaction()
      LocationGuideFlow.LocationGuideLG7 -> LocationGuideLG7Controller.routerTransaction()
      LocationGuideFlow.LocationGuideLG8 -> GenericConfirmationController.getRouterTransaction(
        titleId = R.string.guide_lg8_title,
        descriptionId = R.string.guide_lg8_subtitle,
        buttonId = R.string.generic_close
      )
      LocationGuideFlow.Completed -> null
    }


  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    //noop
  }

  override fun getData() = model
}