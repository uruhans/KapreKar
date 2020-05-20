package com.coloplast.olympus.ui.feature.home

import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.ui.common.Stack
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import timber.log.Timber

class TransmitterManager {

  private val latestTransmitterStates = mutableMapOf<String, Stack<StateChange>>()
  private var currentActiveTransmitter = ""

  fun newStatusVector(stateChange: StateChange): UpdatedStateChange {
    addToLatestStates(stateChange)
    val otherTransmitterOnPatch = otherTransmitterOnPatch(stateChange.getMacAddress())

    if (currentActiveTransmitter.isEmpty()) {
      // first time running - just make this the active transmitter
      currentActiveTransmitter = stateChange.getMacAddress()
    }
    else {
      // the current vector coming in is not our currently active transmitter
      if (currentActiveTransmitter != stateChange.getMacAddress()) {
        // is the new transmitter onPatch?
        if (stateChange.bleDevice.isOnPatch()) {
          // check if other transmitter is also on patch
          if (otherTransmitterOnPatch) {
            // this means that both transmitters are on patch and we don't know which one we should display
            return UpdatedStateChange(
              false,
              StateChange(
                UiState.MULTIPLE_TRANSMITTERS,
                stateChange.overlayState,
                stateChange.bleDevice
              )
            )
          }
          else {
            // make the new transmitter the active transmitter
            setActiveTransmitter(stateChange.getMacAddress())

            latestStateFromOtherTransmitter(stateChange.getMacAddress())?.let {
              return UpdatedStateChange(true, stateChange, it)
            }
          }
        }
      }
      else {
        // it's actually the same transmitter giving us a state update
        // but's it's no longer on patch
        if (!stateChange.bleDevice.isOnPatch()) {
          if (otherTransmitterOnPatch) {
            // however, the other one is on a patch so this is the one we want to make active
            latestStateFromOtherTransmitter(stateChange.getMacAddress())?.let {
              setActiveTransmitter(it.getMacAddress())
              return UpdatedStateChange(true, it, stateChange)
            }
          }
        }
      }
    }

    return UpdatedStateChange(false, stateChange)
  }

  private fun setActiveTransmitter(macAddress: String) {
    Timber.d("new active transmitter: [$currentActiveTransmitter] => [$macAddress]")
    currentActiveTransmitter = macAddress
  }

  private fun addToLatestStates(stateChange: StateChange) {
    val stack = latestTransmitterStates[stateChange.getMacAddress()] ?: Stack(5)
    stack.push(stateChange)
    latestTransmitterStates[stateChange.getMacAddress()] = stack
  }

  private fun otherTransmitterOnPatch(thisTransmitterAddress: String): Boolean {
    val otherTransmitter =
      latestTransmitterStates.entries.firstOrNull { it.key != thisTransmitterAddress }
    return otherTransmitter?.value?.getLatestOrNull()?.bleDevice?.isOnPatch() ?: false
  }

  private fun latestStateFromOtherTransmitter(thisTransmitterAddress: String): StateChange? {
    val otherTransmitter =
      latestTransmitterStates.entries.firstOrNull { it.key != thisTransmitterAddress }
    return otherTransmitter?.value?.getLatestOrNull()
  }

  fun isActiveTransmitter(macAddress: String): Boolean {
    return currentActiveTransmitter == macAddress
  }

}