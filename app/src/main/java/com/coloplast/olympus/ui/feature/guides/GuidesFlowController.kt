package com.coloplast.olympus.ui.feature.guides

import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.BaseFlowController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 17/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class GuidesFlowController: BaseFlowController() {

  init {
    showToolbar.set(true)
  }

  override fun startFlow(flowChildRouter: Router) {
    //flowChildRouter.setRoot(getInitialController())
  }

  /*private fun getInitialController(): RouterTransaction {
    val transaction = GuideMenuController.routerTransaction()
    transaction.controller()
      .targetController = this

    return transaction
  }*/

  companion object{
    private const val CONTROLLER_TAG = "guides_flow_controller"

    fun getRouterTransaction(): RouterTransaction {
      return RouterTransaction.with(GuidesFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}