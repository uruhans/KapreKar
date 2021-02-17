package com.coloplast.olympus.ui.feature.onboarding.valueproposition

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.shared.preferences.PreferencesImpl
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 29/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ValuePropositionFlowController: TopMenuFlowFactoryController<ValuePropositionModel>() {

  private val model: ValuePropositionFactoryImpl by inject()
  private val preferences: PreferencesImpl by inject()

  override fun getFlowFactory(): FlowFactory<ValuePropositionModel> =  model

  override fun onStepCompleted(currentIndex: Int, data: ValuePropositionModel): Boolean {
    if(ValuePropositionFlow.fromId(currentIndex) == ValuePropositionFlow.ValuePropositionVP3) {
      preferences.didSeeValueProposition = true
    }

    return false
  }

  companion object{
    private const val CONTROLLER_TAG = "value_proposition_flow_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(ValuePropositionFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}