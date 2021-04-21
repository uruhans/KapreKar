package com.coloplast.olympus.bluetooth.overrides

import com.coloplast.olympus.patchsplit.models.ApsAction
import com.coloplast.olympus.ui.feature.UpdatedStateChange
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import timber.log.Timber
import java.time.LocalTime

class TimedStateOverride : StateOverride {

  private val timeframeSeconds = 320
  private var lastPatchChange: LocalTime? = null

  override fun handleOverride(updatedStateChange: UpdatedStateChange): UpdatedStateChange {
    return handleWetMount(updatedStateChange)
  }

  /**
  if a leak or blind or pay attention state is registered within 320 seconds after a "split". ie. new patch.
  a check for wet mount should override the initial ui state.

  if two inner sensor are : leak || blinded || pay attention ,ie. has a value of 1,3 or 4 show screen "X"
  if the outer sensor are : leak || blinded || pay attention, ie. has a value of 1,3 or 4 show screen "G"
   **/
  private fun handleWetMount(updatedStateChange: UpdatedStateChange): UpdatedStateChange {
    // if the patch is a brand new patch - ie: we are splitting
    if (updatedStateChange.stateChange.apsAction == ApsAction.Split) {
      // set the timestamp
      lastPatchChange = LocalTime.now()
      // but ignore everything else for now
      return updatedStateChange
    }

    lastPatchChange?.let {
      val timeSincePatchChange = LocalTime.now().minusSeconds(it.second.toLong())
      val isWithinTimeframe = timeSincePatchChange.second <= timeframeSeconds

      if (isWithinTimeframe) {
        updatedStateChange.stateChange.bleDevice.peripheral?.patch?.let {
          val newState = when {
            it.isOuterSensorWet -> UiState.WET_MOUNT_G
            it.isInnerSensorsWet -> UiState.WET_MOUNT_X
            else -> UiState.NO_CHANGE
          }

          if (newState != UiState.NO_CHANGE) {
            Timber.wtf("TimedStateOverride [${updatedStateChange.stateChange.state}] => [$newState]")
            updatedStateChange.stateChange.state = newState
            lastPatchChange = null
          }
        }
      }
      else {
        // if we have passed 320 seconds we dont need this variable any more
        lastPatchChange = null
      }
    }

    return updatedStateChange
  }
}