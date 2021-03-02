package com.coloplast.olympus.ui.feature.guides.flows.location

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 05/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class LocationGuideFactoryImpl : FlowFactory<LocationGuideEmptyModel>() {

  private lateinit var model: LocationGuideEmptyModel

  override fun initialize() {
    model = LocationGuideEmptyModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? =
    when (LocationGuideFlow.fromId(currentIndex)) {
      LocationGuideFlow.LocationGuideLG1 -> InfoPageController.routerTransaction(
        iconId = R.drawable.ic_icon_guides,
        titleId = R.string.guide_lg1_title,
        descriptionId = R.string.guide_lg1_subtitle,
        positiveButtonTextId = R.string.generic_continue
      )
      LocationGuideFlow.LocationGuideLG2 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.location_guide_battery,
        titleId = R.string.guide_lg2_title,
        descriptionId = R.string.guide_lg2_subtitle,
        showQuestionFab = false,
        position = 1,
        totalCount = 6
      )
      LocationGuideFlow.LocationGuideLG3 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.location_guide_disconnection,
        titleId = R.string.guide_lg3_title,
        descriptionId = R.string.guide_lg3_subtitle,
        showQuestionFab = false,
        position = 2,
        totalCount = 6
      )
      LocationGuideFlow.LocationGuideLG4 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_c4_alwaysallow_android,
        titleId = R.string.guide_lg4_title,
        descriptionId = R.string.guide_lg4_subtitle,
        showQuestionFab = false,
        position = 3,
        totalCount = 6
      )
      LocationGuideFlow.LocationGuideLG5 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.location_guide_permission,
        titleId = R.string.guide_lg5_title,
        descriptionId = R.string.guide_lg5_subtitle,
        showQuestionFab = false,
        position = 4,
        totalCount = 6
      )
      LocationGuideFlow.LocationGuideLG6 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.location_guide_privacy,
        titleId = R.string.guide_lg6_title,
        descriptionId = R.string.guide_lg6_subtitle,
        showQuestionFab = false,
        position = 5,
        totalCount = 6
      )
      LocationGuideFlow.LocationGuideLG7 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.location_guide_battery_drain,
        titleId = R.string.guide_lg7_title,
        descriptionId = R.string.guide_lg7_subtitle,
        showQuestionFab = false,
        position = 6,
        totalCount = 6
      )
      LocationGuideFlow.LocationGuideLG8 -> GenericConfirmationController.routerTransaction(
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