package com.coloplast.olympus.ui.feature

import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.services.OlympusWirelessProtocol
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.shared.moshi.deserialize
import com.coloplast.olympus.shared.moshi.serialize
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.Stack
import com.coloplast.olympus.ui.feature.home.models.Stitch
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import timber.log.Timber

/**
 * This manager determines which Tx is currently the *active* one (ie: the one whose state is being displayed on the status screen)
 */
class TransmitterManager(private val bluetoothManager: BluetoothManager) {

  private val transmitterStateHistory = mutableMapOf<String, Stack<StateChange>>()
  private var activeTransmitter
    get() = Preferences.get().activeTransmitter
    set(value) {
      Preferences.get().activeTransmitter = value
    }

  init {
    // TODO - this may need to change at a later stage to come from DB instead
    Preferences.get().latestStateFromActiveTx.let { latestStateFromActiveTx ->
      if(latestStateFromActiveTx.isEmpty()) {
        return@let
      }

      latestStateFromActiveTx.deserialize<StateChange>()?.let { stateChange ->
        transmitterStateHistory[activeTransmitter] = Stack<StateChange>(5).also {
          it.push(stateChange)
        }
      }
    }
  }

  /**
   * Determine the currently active transmitter and returns the latest stateChange from said active transmitter
   *  - Stores the stateChange for later use
   *  - Incoming stateChange might not be from the same transmitter as the one being returned
   */
  fun determineCurrentlyActiveTransmitterAndReturnStateChangeFromIt(stateChange: StateChange): UpdatedStateChange {
    addToHistory(stateChange)

    // we verify that the currently active transmitter is actually connected
    // there could be an edge case where it may not be connected - but we still have a history of it's states
    // that may cause false positives
    val isActiveTransmitterConnected = bluetoothManager.isConnected(activeTransmitter)

    if(!isActiveTransmitterConnected || activeTransmitter.isEmpty()) {
      // we have no active transmitters
      return if(isCurrentlyAttached(stateChange)) {
        setActiveTransmitter(stateChange)
        UpdatedStateChange(stateChange, true)
      }
      else {
        UpdatedStateChange(StateChange(UiState.TRANSMITTER_DETACHED_V, stateChange.overlayState, stateChange.bleDevice))
      }
    }
    else {
      // we do have an active transmitter
      if(isActiveTransmitter(stateChange.getMacAddress())) {
        // the current tx is already active
        if(isCurrentlyAttached(stateChange)) {
          // it's also already attached
          return UpdatedStateChange(stateChange)
        }
        else {
          // is the other transmitter on patch
          return if(isOtherTransmitterAttached(stateChange.getMacAddress())) {
            val otherTxState = latestStateFromOtherTransmitter(stateChange.getMacAddress())!!
            setActiveTransmitter(otherTxState)
            UpdatedStateChange(otherTxState, true)
          }
          else {
            // no transmitters are currently on patch
            UpdatedStateChange(stateChange)
          }
        }
      }
      else {
        // this is not the active transmitter, but it is attached
        return if(isCurrentlyAttached(stateChange)) {
          // is the other transmitter also attached?
          if(isOtherTransmitterAttached(stateChange.getMacAddress())) {
            UpdatedStateChange(StateChange( UiState.MULTIPLE_TRANSMITTERS, stateChange.overlayState, stateChange.bleDevice))
          }
          else {
            setActiveTransmitter(stateChange)
            UpdatedStateChange(stateChange, true)
          }
        }
        else {
          // no - this is not attached
          val otherTxState = latestStateFromOtherTransmitter(stateChange.getMacAddress())!!
          UpdatedStateChange(otherTxState)
        }
      }
    }
  }

  /**
   * Determines whether or not the user needs to be asked whether or not the transmitter has been put onto a new or an old patch
   *  - this is used for reset (when new) and stitch (when old)
   */
  fun determineTransmitterPatchQuestioning(updatedStateChange: UpdatedStateChange) : StateChange {
    if(updatedStateChange.activeTransmitterChanged) {
      return StateChange(updatedStateChange.stateChange.state, OverlayState.QUESTION, updatedStateChange.stateChange.bleDevice)
    }
    else {
      if(updatedStateChange.stateChange.state != UiState.MULTIPLE_TRANSMITTERS) {
        if (!attachedPreviously(updatedStateChange.stateChange.getMacAddress())) {
          if (isCurrentlyAttached(updatedStateChange.stateChange)) {
            return StateChange(updatedStateChange.stateChange.state, OverlayState.QUESTION, updatedStateChange.stateChange.bleDevice)
          }
        }
      }

      return updatedStateChange.stateChange
    }
  }

  /**
   * @return active transmitter macAddress
   */
  fun activeTransmitterMacAddress() : String {
    return activeTransmitter
  }

  /**
   * get stitch bytes from other transmitter (used when changing Tx onto the same patch)
   *  - the other Tx being the one that is *not* active
   *  @return a Stitch object containing the actual stitching or a "Reset" package instead (first time the user is asked both answers do a reset)
   */
  fun getStitchBytesFromOtherTransmitter() : Stitch {
    val otherTxState = latestStateFromOtherTransmitter(activeTransmitter)

    return Stitch(
      activeTransmitter,
      otherTxState?.bleDevice?.peripheral?.getStitchArray() ?: OlympusWirelessProtocol.Characteristics.RESET_STATUS_DATA)
  }

  private fun setActiveTransmitter(stateChange: StateChange) {
    Timber.d("new active transmitter: [$activeTransmitter] => [${stateChange.getMacAddress()}]")
    activeTransmitter = stateChange.getMacAddress()

    // TODO - this may need to change at a later stage - should possibly be stored in Local DB
    // We store it here, because the current active TX may have changed from beginning to this place
    Preferences.get().latestStateFromActiveTx = stateChange.serialize()
  }

  private fun addToHistory(stateChange: StateChange) {
    val stack = transmitterStateHistory[stateChange.getMacAddress()] ?: Stack(5)
    stack.push(stateChange)
    transmitterStateHistory[stateChange.getMacAddress()] = stack
  }

  private fun isOtherTransmitterAttached(thisTransmitterAddress: String): Boolean {
    val otherTransmitter =
      transmitterStateHistory.entries.firstOrNull { it.key != thisTransmitterAddress }

    // verify that the *other* transmitter is actually connected
    val isConnected = bluetoothManager.isConnected(otherTransmitter?.key ?: "")
    return isConnected && otherTransmitter?.value?.peek()?.bleDevice?.isAttached() ?: false
  }

  private fun latestStateFromOtherTransmitter(thisTransmitterAddress: String): StateChange? {
    val otherTransmitter =
      transmitterStateHistory.entries.firstOrNull { it.key != thisTransmitterAddress }

    // verify that the *other* transmitter is actually connected
    val isConnected = bluetoothManager.isConnected(otherTransmitter?.key ?: "")
    return if(isConnected) otherTransmitter?.value?.peek() else null
  }

  private fun attachedPreviously(macAddress: String) : Boolean {
    val transmitter = transmitterStateHistory.entries.firstOrNull { it.key == macAddress }

    // verify that the transmitter is actually connected
    val isConnected = bluetoothManager.isConnected(transmitter?.key ?: "")
    return isConnected && transmitter?.value?.peekPrevious()?.bleDevice?.isAttached() ?: false
  }

  private fun isCurrentlyAttached(stateChange: StateChange) : Boolean {
    return stateChange.bleDevice.isAttached()
  }

  /**
   * whether or not the incoming Tx macAddress is the currently active Tx
   */
  fun isActiveTransmitter(macAddress: String): Boolean {
    return activeTransmitter == macAddress
  }

  /**
   * Tell the manager that a Tx Disconnect occurred
   * @return latest State from other transmitter or NULL if we only have one Tx
   */
  fun onTransmitterDisconnect(disconnectedTransmitterMacAddress: String) : StateChange? {
    var shouldDispatchOtherTxState = false

    // if this is the currently active transmitter
    if(isActiveTransmitter(disconnectedTransmitterMacAddress)) {
      // set the current transmitter to nothing
      activeTransmitter = ""

      // check if the other transmitter is attached
      if(isOtherTransmitterAttached(disconnectedTransmitterMacAddress)) {
        shouldDispatchOtherTxState = true
      }
      else {
        // we need to send back a state where we tell the user that the transmitter has lost connection
        transmitterStateHistory[disconnectedTransmitterMacAddress]?.peek()?.let {
          return StateChange(UiState.NO_CHANGE, OverlayState.BLE_CONNECTION_LOST, it.bleDevice)
        }
      }
    }
    else {
      val currentTxUpdate = transmitterStateHistory.entries.firstOrNull { it.key == disconnectedTransmitterMacAddress }
      if(currentTxUpdate?.value?.peek()?.bleDevice?.isAttached() == true) {
        shouldDispatchOtherTxState = true
      }
    }

    if(shouldDispatchOtherTxState) {
      // return latest stateUpdate from the other transmitter
      return latestStateFromOtherTransmitter(disconnectedTransmitterMacAddress)
    }

    return null
  }
}