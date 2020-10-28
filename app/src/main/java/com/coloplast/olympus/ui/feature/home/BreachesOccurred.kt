package com.coloplast.olympus.ui.feature.home

import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
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
      breaches.add(Breach(LocalDateTime.now().toMillis(), stateChange.bleDevice.peripheral?.patch!!))
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
    if(stateChange.state == UiState.NO_CHANGE || stateChange.state == UiState.INNER_BREACH_B || stateChange.state == UiState.OUTER_BREACH_C) {
      // the reason we have to check the actual patch here, is because the statemachine may have
      // changed the UiState to UiState.NO_CHANGE (this would occur if we got two inner leaks in a row (OK -> INNER_LEAK -> NO_CHANGE)
      val currentPatch = stateChange.bleDevice.peripheral?.patch
      val state = currentPatch?.getState()

      if (state != PatchState.INNER_LEAK && state != PatchState.OUTER_LEAK) {
        return false
      }

      val previousPatch = getBreaches().lastOrNull()?.patch

      if (state == PatchState.INNER_LEAK) {
        if (previousPatch != null) {
          // we need to check if the sensors have changed from last breach to this one - because the UiState will be INNER LEAK no matter what from this moment on
          if (previousPatch.innerSensor1 != currentPatch.innerSensor1
            || previousPatch.innerSensor2 != currentPatch.innerSensor2
            || previousPatch.innerSensor3 != currentPatch.innerSensor3
          ) {
            // there has been a change on the inner sensors
            return true
          }

          // we have an inner leak, but it's the same sensor as before, so no new breach
          return false
        }
        else {
          // first time we have an inner leak
          return true
        }
      }

      return state == PatchState.OUTER_LEAK
    }

    return false
  }

  private fun updateStore() {
    preferences.leaks = getBreaches().serializeList()
  }
}

class Breach(val localDateTime: Long, val patch: Patch) : MoshiSerializable