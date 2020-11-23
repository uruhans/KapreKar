package com.coloplast.olympus.ui.feature.onboarding.setup

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.InfoPageSmallButtonController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.models.flow.EmptyModel
import com.coloplast.olympus.ui.feature.onboarding.setup.connection.SetupGuideLocationSGC4Controller
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideConnectionFlow
import com.coloplast.olympus.ui.feature.transmitters.add.AddTransmitterFlowController

class SetupGuideConnectionFactoryImpl : FlowFactory<EmptyModel>() {

  private val model = EmptyModel()

  override fun initialize() {
    /* no-op */
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when(SetupGuideConnectionFlow.fromIndex(currentIndex)) {
      SetupGuideConnectionFlow.SetupGuideSG3 -> SetupGuideSG3.routerTransaction()
      SetupGuideConnectionFlow.SetupGuideBluetoothSGC1 -> {
        InfoPageSmallButtonController.routerTransaction(
          R.drawable.ic_bluetooth,
          R.string.onboarding_chapter_intro_bluetooth_header,
          R.string.onboarding_chapter_intro_bluetooth_description,
          showCloseButton = false
        )
      }
      SetupGuideConnectionFlow.SetupGuideLocationSGC3 -> {
        InfoPageSmallButtonController.routerTransaction(
          R.drawable.ic_location,
          R.string.onboarding_chapter_connecting_avoid_leaks_title,
          R.string.onboarding_chapter_connecting_avoid_leaks_description,
          showCloseButton = false
        )
      }
      SetupGuideConnectionFlow.SetupGuideLocationAllowSGC4 -> SetupGuideLocationSGC4Controller.routerTransaction()
      SetupGuideConnectionFlow.SetupGuideFindTMCodeSGC5 -> AddTransmitterFlowController.routerTransaction(2)
      SetupGuideConnectionFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {

  }

  override fun getData(): EmptyModel {
    return model
  }
}