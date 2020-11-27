package com.coloplast.olympus.ui.feature.guides.flows.tour

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.models.flow.EmptyModel


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
        positiveButtonTextId = R.string.generic_continue
      )
      AppTourFlow.AppTourAP2 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.guide_app_tour_status_screen,
        titleId = R.string.guide_ap2_title,
        descriptionId = R.string.guide_ap2_subtitle,
        position = 1,
        totalCount = 11
      )
      AppTourFlow.AppTourAP3 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.guide_app_tour_inner_sensor_leak_status,
        titleId = R.string.guide_ap3_title,
        descriptionId = R.string.guide_ap3_subtitle,
        position = 2,
        totalCount = 11
      )
      AppTourFlow.AppTourAP4 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.guide_app_tour_outer_sensor_leak_status,
        titleId = R.string.guide_ap4_title,
        descriptionId = R.string.guide_ap3_subtitle,
        position = 3,
        totalCount = 11
      )
      AppTourFlow.AppTourAP5 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.guide_app_tour_detailed_view,
        titleId = R.string.guide_ap5_title,
        descriptionId = R.string.guide_ap5_subtitle,
        position = 4,
        totalCount = 11
      )
      AppTourFlow.AppTourAP6 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.guide_app_tour_transmitter_battery,
        titleId = R.string.guide_ap6_title,
        descriptionId = R.string.guide_ap6_subtitle,
        position = 5,
        totalCount = 11
      )
      AppTourFlow.AppTourAP7 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.guide_app_tour_menu,
        titleId = R.string.guide_ap7_title,
        descriptionId = R.string.guide_ap7_subtitle,
        position = 6,
        totalCount = 11
      )
      AppTourFlow.AppTourAP8 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.guide_app_tour_guides,
        titleId = R.string.guide_ap8_title,
        descriptionId = R.string.guide_ap8_subtitle,
        position = 7,
        totalCount = 11
      )
      AppTourFlow.AppTourAP9 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.guide_app_tour_applying_guide,
        titleId = R.string.guide_ap9_title,
        descriptionId = R.string.guide_ap9_subtitle,
        position = 8,
        totalCount = 11
      )
      AppTourFlow.AppTourAP10 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.guide_app_tour_settings_menu,
        titleId = R.string.guide_ap10_title,
        descriptionId = R.string.guide_ap10_subtitle,
        position = 9,
        totalCount = 11
      )
      AppTourFlow.AppTourAP11 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.guide_app_tour_silent_mode,
        titleId = R.string.guide_ap11_title,
        descriptionId = R.string.guide_ap11_subtitle,
        position = 10,
        totalCount = 11
      )
      AppTourFlow.AppTourAP12 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.guide_app_tour_schedule_bedtime,
        titleId = R.string.guide_ap12_title,
        descriptionId = R.string.guide_ap12_subtitle,
        position = 11,
        totalCount = 11
      )
      AppTourFlow.AppTourAP13 -> GenericConfirmationController.routerTransaction(
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