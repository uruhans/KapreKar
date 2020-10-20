package com.coloplast.olympus.ui.feature.guides

import android.os.Bundle
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.BaseFlowController
import com.coloplast.olympus.ui.feature.guides.articles.GuideController
import com.coloplast.olympus.ui.feature.guides.articles.models.GuideType

class SingleGuideFlowController(args: Bundle): BaseFlowController() {

  private val guideType = GuideType.fromId(args.getInt(BUNDLE_GUIDE_TYPE))

  init {
    showToolbar.set(true)
  }

  override fun startFlow(flowChildRouter: Router) {
    flowChildRouter.setRoot(getInitialController())
  }

  private fun getInitialController(): RouterTransaction {
    return GuideController.routerTransaction(guideType)
  }

  companion object{
    private const val CONTROLLER_TAG = "single_guide_flow_controller"
    private const val BUNDLE_GUIDE_TYPE = "bundle_guide_type"

    fun routerTransaction(guideType: GuideType): RouterTransaction {
      with(Bundle()) {
        putInt(BUNDLE_GUIDE_TYPE, guideType.id)
        return RouterTransaction.with(SingleGuideFlowController(this)).tag(CONTROLLER_TAG)
      }
    }
  }
}