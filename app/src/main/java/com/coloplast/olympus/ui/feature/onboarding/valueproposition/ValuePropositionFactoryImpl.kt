package com.coloplast.olympus.ui.feature.onboarding.valueproposition

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 29/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ValuePropositionFactoryImpl: FlowFactory<ValuePropositionModel>() {

  private lateinit var model: ValuePropositionModel

  override fun initialize() {
    model = ValuePropositionModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when(ValuePropositionFlow.fromIndex(currentIndex)) {
      ValuePropositionFlow.ValuePropositionVP1 -> { null }
      ValuePropositionFlow.ValuePropositionVP2 -> { null }
      ValuePropositionFlow.ValuePropositionVP3 -> { null }
      ValuePropositionFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    //noop
  }

  override fun getData(): ValuePropositionModel = model
}