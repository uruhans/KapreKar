package com.coloplast.olympus.ui.feature

import com.coloplast.olympus.bluetooth.businesslayer.services.OlympusWirelessProtocol
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.ui.common.Stack
import com.coloplast.olympus.ui.feature.home.models.Stitch
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import timber.log.Timber

/**
 * This manager determines which Tx is currently the *active* one (ie: the one whose state is being displayed on the status screen)
 */
class TransmitterManager {

  private val latestTransmitterStates = mutableMapOf<String, Stack<StateChange>>()
  private var currentActiveTransmitter = ""

  fun stateChangeFromActiveTransmitter(stateChange: StateChange): UpdatedStateChange {
    addToLatestStates(stateChange)

    if(currentActiveTransmitter.isEmpty()) {
      // we have no active transmitters
      return if(isAttached(stateChange)) {
        setActiveTransmitter(stateChange)
        UpdatedStateChange(true, stateChange)
      }
      else {
        UpdatedStateChange(false, StateChange(UiState.TRANSMITTER_DETACHED_V, stateChange.overlayState, stateChange.bleDevice))
      }
    }
    else {
      // we do have an active transmitter
      if(isActiveTransmitter(stateChange.getMacAddress())) {
        // the current tx is already active
        if(isAttached(stateChange)) {
          // it's also already attached
          return UpdatedStateChange(false, stateChange)
        }
        else {
          // is the other transmitter on patch
          return if(isOtherTransmitterAttached(stateChange.getMacAddress())) {
            val otherTxState = latestStateFromOtherTransmitter(stateChange.getMacAddress())!!
            setActiveTransmitter(otherTxState)
            UpdatedStateChange(true, otherTxState)
          }
          else {
            // no transmitters are currently on patch
            UpdatedStateChange(false, stateChange)
          }
        }
      }
      else {
        // this is not the active transmitter, but it is attached
        return if(isAttached(stateChange)) {
          // is the other transmitter also attached?
          if(isOtherTransmitterAttached(stateChange.getMacAddress())) {
            UpdatedStateChange(false, StateChange( UiState.MULTIPLE_TRANSMITTERS, stateChange.overlayState, stateChange.bleDevice))
          }
          else {
            setActiveTransmitter(stateChange)
            UpdatedStateChange(true, stateChange)
          }
        }
        else {
          // no - this is not attached
          val otherTxState = latestStateFromOtherTransmitter(stateChange.getMacAddress())!!
          UpdatedStateChange(false, otherTxState)
        }
      }
    }
  }
  
  fun determineTxLayerQuestioning(updatedStateChange: UpdatedStateChange) : StateChange {
    if(updatedStateChange.activeTransmitterChanged) {
      return StateChange(updatedStateChange.stateChange.state, OverlayState.QUESTION, updatedStateChange.stateChange.bleDevice)
    }
    else {
      if(updatedStateChange.stateChange.state != UiState.MULTIPLE_TRANSMITTERS) {
        if (!wasAttachedBefore(updatedStateChange.stateChange.getMacAddress())) {
          if (isAttached(updatedStateChange.stateChange)) {
            return StateChange(updatedStateChange.stateChange.state, OverlayState.QUESTION, updatedStateChange.stateChange.bleDevice)
          }
        }
      }

      return updatedStateChange.stateChange
    }
  }

  fun activeTransmitterMacAddress() : String {
    return currentActiveTransmitter
  }

  fun getStitchArrayFromOther() : Stitch? {
    val otherTxState = latestStateFromOtherTransmitter(currentActiveTransmitter)

    return Stitch(
      currentActiveTransmitter,
      otherTxState?.bleDevice?.peripheral?.getStitchArray() ?: OlympusWirelessProtocol.Characteristics.RESET_STATUS_DATA)
  }

  private fun setActiveTransmitter(stateChange: StateChange) {
    Timber.d("new active transmitter: [$currentActiveTransmitter] => [${stateChange.getMacAddress()}]")
    currentActiveTransmitter = stateChange.getMacAddress()
  }

  private fun addToLatestStates(stateChange: StateChange) {
    val stack = latestTransmitterStates[stateChange.getMacAddress()] ?: Stack(5)
    stack.push(stateChange)
    latestTransmitterStates[stateChange.getMacAddress()] = stack
  }

  private fun isOtherTransmitterAttached(thisTransmitterAddress: String): Boolean {
    val otherTransmitter =
      latestTransmitterStates.entries.firstOrNull { it.key != thisTransmitterAddress }
    return otherTransmitter?.value?.peek()?.bleDevice?.isAttached() ?: false
  }

  private fun latestStateFromOtherTransmitter(thisTransmitterAddress: String): StateChange? {
    val otherTransmitter =
      latestTransmitterStates.entries.firstOrNull { it.key != thisTransmitterAddress }
    return otherTransmitter?.value?.peek()
  }

  private fun wasAttachedBefore(macAddress: String) : Boolean {
    val transmitter = latestTransmitterStates.entries.firstOrNull { it.key == macAddress }
    return transmitter?.value?.peekPrevious()?.bleDevice?.isAttached() ?: false
  }

  private fun isAttached(stateChange: StateChange) : Boolean {
    return stateChange.bleDevice.isAttached()
  }

  fun isActiveTransmitter(macAddress: String): Boolean {
    return currentActiveTransmitter == macAddress
  }

  fun transmitterDisconnected(macAddress: String) : StateChange? {
    var shouldDispatchOtherTxState = false

    // if this is the currently active transmitter
    if(isActiveTransmitter(macAddress)) {
      // set the current transmitter to nothing
      currentActiveTransmitter = ""

      // check if the other transmitter is attached
      if(isOtherTransmitterAttached(macAddress)) {
        shouldDispatchOtherTxState = true
      }
    }
    else {
      val currentTxUpdate = latestTransmitterStates.entries.firstOrNull { it.key == macAddress }
      if(currentTxUpdate?.value?.peek()?.bleDevice?.isAttached() == true) {
        shouldDispatchOtherTxState = true
      }
    }

    if(shouldDispatchOtherTxState) {
      // dispatch it's latest stateUpdate (which triggers the whole process again)
      latestStateFromOtherTransmitter(macAddress)?.let {
        return it
      }
    }

    return null
  }

}