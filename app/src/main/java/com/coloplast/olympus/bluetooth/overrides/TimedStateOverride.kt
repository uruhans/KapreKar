package com.coloplast.olympus.bluetooth.overrides

import com.coloplast.olympus.patchsplit.models.ApsAction
import com.coloplast.olympus.ui.feature.UpdatedStateChange
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import timber.log.Timber
import java.time.Instant

class TimedStateOverride : StateOverride {

  private val timeframeSeconds = 320
  private var lastPatchChange: Instant? = null

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
      lastPatchChange = Instant.now()
      // but ignore everything else for now
      return updatedStateChange
    }

    lastPatchChange?.let {
      val timeSincePatchChange = Instant.now().epochSecond.minus(it.epochSecond)
      val isWithinTimeframe = timeSincePatchChange <= timeframeSeconds

      if (isWithinTimeframe) {
        updatedStateChange.stateChange.bleDevice.peripheral?.patch?.let { patch ->
          val newState = when {
            patch.isOuterSensorWet -> UiState.WET_MOUNT_G
            patch.isInnerSensorsWet -> UiState.WET_MOUNT_X
            else -> UiState.NO_CHANGE
          }

          if (newState != UiState.NO_CHANGE) {
            Timber.wtf("TimedStateOverride [${updatedStateChange.stateChange.state}] => [$newState]")
            updatedStateChange.stateChange.state = newState
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