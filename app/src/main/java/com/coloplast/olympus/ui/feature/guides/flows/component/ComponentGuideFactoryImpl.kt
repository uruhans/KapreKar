package com.coloplast.olympus.ui.feature.guides.flows.component

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.models.flow.EmptyModel


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
        positiveButtonTextId = R.string.generic_continue
      )
      ComponentGuideFlow.ComponentGuideCO2 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.sg_i6_whatsinthebox,
        titleId = R.string.guide_co2_title,
        descriptionId = R.string.guide_co2_description,
        position = 1,
        totalCount = 12
      )
      ComponentGuideFlow.ComponentGuideCO3 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.sg_i7_sensorlayer,
        titleId = R.string.guide_co3_title,
        descriptionId = R.string.guide_co3_description,
        position = 2,
        totalCount = 12
      )
      ComponentGuideFlow.ComponentGuideCO4 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i8_advsensortech,
        titleId = R.string.guide_co4_title,
        descriptionId = R.string.guide_co4_description,
        position = 3,
        totalCount = 12
      )
      ComponentGuideFlow.ComponentGuideCO5 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i9_inneroutersensor,
        titleId = R.string.guide_co5_title,
        descriptionId = R.string.guide_co5_description,
        position = 4,
        totalCount = 12
      )
      ComponentGuideFlow.ComponentGuideCO6 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i10_connectorflap,
        titleId = R.string.guide_co6_title,
        descriptionId = R.string.guide_co6_description,
        position = 5,
        totalCount = 12
      )
      ComponentGuideFlow.ComponentGuideCO7 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.sg_i11_yourtransmitter,
        titleId = R.string.guide_co7_title,
        descriptionId = R.string.guide_co7_description,
        position = 6,
        totalCount = 12
      )
      ComponentGuideFlow.ComponentGuideCO8 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i12_openingtmlid,
        titleId = R.string.guide_co8_title,
        descriptionId = R.string.guide_co8_description,
        position = 7,
        totalCount = 12
      )
      ComponentGuideFlow.ComponentGuideCO9 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i13_tm_opening,
        titleId = R.string.guide_co9_title,
        descriptionId = R.string.guide_co9_description,
        position = 8,
        totalCount = 12
      )
      ComponentGuideFlow.ComponentGuideCO10 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i14_disengaging_tm_lid,
        titleId = R.string.guide_co10_title,
        descriptionId = R.string.guide_co10_description,
        position = 9,
        totalCount = 12
      )
      ComponentGuideFlow.ComponentGuideCO11 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i15_closing_tm_lid,
        titleId = R.string.guide_co11_title,
        descriptionId = R.string.guide_co11_description,
        position = 10,
        totalCount = 12
      )
      ComponentGuideFlow.ComponentGuideCO12 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i16_chargerandcable,
        titleId = R.string.guide_co12_title,
        descriptionId = R.string.guide_co12_description,
        position = 11,
        totalCount = 12
      )
      ComponentGuideFlow.ComponentGuideCO13 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.sg_i17_chargingandpairing,
        titleId = R.string.guide_co13_title,
        descriptionId = R.string.guide_co13_description,
        position = 12,
        totalCount = 12
      )
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