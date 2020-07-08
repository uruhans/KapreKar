package com.coloplast.olympus.ui.feature.onboarding.valueproposition

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.SimpleInfoPageController
import com.coloplast.olympus.ui.common.flow.FlowFactory


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 29/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ValuePropositionFactoryImpl : FlowFactory<ValuePropositionModel>() {

  private lateinit var model: ValuePropositionModel

  override fun initialize() {
    model = ValuePropositionModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when (ValuePropositionFlow.fromId(currentIndex)) {
      ValuePropositionFlow.ValuePropositionVP1 -> {
        SimpleInfoPageController.routerTransaction(
          iconId = R.drawable.ic_sidemenu_status,
          titleId = R.string.onboarding_value_proposition_screen_1_title,
          desciptionId = R.string.onboarding_value_proposition_screen_1_description,
          position = 1,
          totalCount = 3,
          showBackButton = false,
          showCloseButton = false
        )
      }
      ValuePropositionFlow.ValuePropositionVP2 -> {
        SimpleInfoPageController.routerTransaction(
          iconId = R.drawable.ic_bulb,
          titleId = R.string.onboarding_value_proposition_screen_2_title,
          desciptionId = R.string.onboarding_value_proposition_screen_2_description,
          position = 2,
          totalCount = 3,
          showBackButton = true,
          showCloseButton = false
        )
      }
      ValuePropositionFlow.ValuePropositionVP3 -> {
        SimpleInfoPageController.routerTransaction(
          iconId = R.drawable.ic_notifications,
          titleId = R.string.onboarding_value_proposition_screen_3_title,
          desciptionId = R.string.onboarding_value_proposition_screen_3_description,
          position = 3,
          totalCount = 3,
          showBackButton = true,
          showCloseButton = false
        )
      }
      else -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    //noop
  }

  override fun getData(): ValuePropositionModel = model
}