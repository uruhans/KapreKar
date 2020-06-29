package com.coloplast.olympus.ui.feature.onboarding.valueproposition


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 29/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class ValuePropositionFlow {
  ValuePropositionVP1,
  ValuePropositionVP2,
  ValuePropositionVP3,
  Completed;

  companion object {
    fun fromIndex(index: Int) : ValuePropositionFlow {
      return values().associateBy { flow: ValuePropositionFlow -> flow.ordinal}[index] ?: Completed
    }
  }
}