package com.coloplast.olympus.ui.feature.home

import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.shared.moshi.*
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.Stack
import com.coloplast.olympus.ui.feature.statemachine.models.UiState

class BreachesOccurred {

  init {
    Preferences.get().leaks.deserializeList<StateChange>()?.let {
      breaches.addAll(it)
    }
  }

  private val breaches : MutableList<StateChange> = mutableListOf<StateChange>()

  fun add(stateChange: StateChange) {
    if(isBreach(stateChange)) {
      breaches.add(stateChange)
      updateStore()
    }
  }

  fun getBreaches() : List<StateChange> {
    val size = breaches.size

    return when {
      size == 1 -> listOf(breaches[0])
      size > 2 -> breaches.subList(0, 2).toList()
      else -> emptyList()
    }
  }

  private fun isBreach(stateChange: StateChange) : Boolean {
    return stateChange.state == UiState.INNER_BREACH_B || stateChange.state == UiState.OUTER_BREACH_C
  }

  private fun updateStore() {
    Preferences.get().leaks = breaches.
  }

}