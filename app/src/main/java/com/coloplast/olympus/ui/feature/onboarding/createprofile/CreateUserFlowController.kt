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

  override fun onStepCompleted(currentIndex: Int, data: CreateUserModel): Boolean {
    return when(CreateUserFlow.fromIndex(currentIndex-1)){
      CreateUserFlow.PhoneSU6 -> {
        if(data.phoneNumber.contains(UK_COUNTRY_CODE_1) || data.phoneNumber.contains(UK_COUNTRY_CODE_2)){
          //TODO: skip insurance data
          getFlowFactory().next()
          true
        }else {
          false // go to next step as usual.
        }
      }
      else -> super.onStepCompleted(currentIndex, data)
    }
  }

  companion object{
    private const val CONTROLLER_TAG = "create_user_flow_controller"
    private const val UK_COUNTRY_CODE_1 = "+44"
    private const val UK_COUNTRY_CODE_2 = "0044"

    fun routerTransaction() = RouterTransaction.with(CreateUserFlowController())
      .tag(CONTROLLER_TAG)
  }
}