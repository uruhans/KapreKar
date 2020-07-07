package com.coloplast.olympus.ui.feature.onboarding.createprofile

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.FlowFactoryController
import com.coloplast.olympus.ui.feature.onboarding.createprofile.models.CreateUserModel
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 30/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class CreateUserFlowController: FlowFactoryController<CreateUserModel>() {

  private val createUserFactory: FlowFactory<CreateUserModel> by inject()

  override fun getFlowFactory(): FlowFactory<CreateUserModel> = createUserFactory

  companion object{
    private const val CONTROLLER_TAG = "create_user_flow_controller"

    fun routerTransaction() = RouterTransaction.with(CreateUserFlowController())
      .tag(CONTROLLER_TAG)
  }
}