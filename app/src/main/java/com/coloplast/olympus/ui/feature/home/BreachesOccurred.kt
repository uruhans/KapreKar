package com.coloplast.olympus.ui.feature.home

import com.coloplast.olympus.bluetooth.businesslayer.models.PatchState
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.shared.moshi.MoshiSerializable
import com.coloplast.olympus.shared.moshi.deserializeList
import com.coloplast.olympus.shared.moshi.serializeList
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.extensions.toMillis
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import java.time.LocalDateTime

class BreachesOccurred(private val preferences: Preferences) {

  private val breaches = mutableListOf<Breach>()

  init {
    preferences.leaks.let { leaks ->
      if(leaks.isEmpty()) {
        return@let
      }

      leaks.deserializeList<Breach>()?.let {
        breaches.addAll(it)
      }
    }
  }

  fun add(stateChange: StateChange) {
    if(isBreach(stateChange)) {
      breaches.add(Breach(LocalDateTime.now().toMillis()))
      updateStore()
    }
    else if(stateChange.state == UiState.OK_A) {
      clear()
    }
  }

  fun getBreaches() : List<Breach> {
    val size = breaches.size

    return when {
      size == 1 -> listOf(breaches[0])
      size >= 2 -> breaches.subList(0, 2).toList()
      else -> emptyList()
    }
  }

  fun clear() {
    breaches.clear()
    preferences.leaks = ""
  }

  private fun isBreach(stateChange: StateChange) : Boolean {
    // the reason we have to check the actual patch here, is because the statemachine may have
    // changed the UiState to UiState.NO_CHANGE (this would occur if we got two inner leaks in a row (OK -> INNER_LEAK -> NO_CHANGE)
    val state = stateChange.bleDevice.peripheral?.patch?.getState()
    return state == PatchState.INNER_LEAK || state == PatchState.OUTER_LEAK
  }

  private fun updateStore() {
    preferences.leaks = getBreaches().serializeList()
  }
}

class Breach(val localDateTime: Long) : MoshiSerializable