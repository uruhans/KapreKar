package com.coloplast.olympus.ui.feature

import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.patchsplit.models.LatestDetachVector
import com.coloplast.olympus.shared.moshi.deserialize
import com.coloplast.olympus.shared.moshi.serialize
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.Stack
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * This manager determines which Tx is currently the *active* one (ie: the one whose state is being displayed on the status screen)
 */
class TransmitterManager(
  private val preferences: Preferences,
  val bluetoothManager: BluetoothManager
) {

  private var latestDetachVector: LatestDetachVector? = null

  private val transmitterStateHistory = mutableMapOf<String, Stack<StateChange>>()
  private var activeTransmitter
    get() = preferences.activeTransmitter
    set(value) {
      preferences.activeTransmitter = value
    }

  private var wasPreviousUiStateMultipleTx = false


  init {
    preferences.latestStateFromActiveTx.let { latestStateFromActiveTx ->
      if(latestStateFromActiveTx.isEmpty()) {
        return@let
      }

      latestStateFromActiveTx.deserialize<StateChange>()?.let { stateChange ->
        transmitterStateHistory[activeTransmitter] = Stack<StateChange>(3).also {
          it.push(stateChange)
        }
      }

      transmitterStateHistory.remove("")
    }

    preferences.latestDetach.let { latestDetach ->
      if(latestDetach.isEmpty()) {
        return@let
      }

      latestDetachVector = latestDetach.deserialize()
    }
  }

  /**
   * Determine the currently active transmitter and returns the latest stateChange from said active transmitter
   *  - Stores the stateChange for later use
   *  - Incoming stateChange might not be from the same transmitter as the one being returned
   */
  fun determineCurrentlyActiveTransmitterAndReturnStateChangeFromIt(stateChange: StateChange): UpdatedStateChange? {
    addToHistory(stateChange)

    val otherConnectedAndAttached = isOtherConnectedAndAttached(stateChange.getMacAddress())

    if(!hasActiveTransmitter()) {
      wasPreviousUiStateMultipleTx = false
      return if(isCurrentlyAttached(stateChange)) {
        // change active tx to this one
        setActiveTransmitter(stateChange)
        // return it's state
        UpdatedStateChange(stateChange, true)
      }
      else {
        // we have no active tx - nor is the current on attached - force UiState DETACHED
        UpdatedStateChange(StateChange(UiState.TRANSMITTER_DETACHED_V, stateChange.overlayState, stateChange.bleDevice, timestamp = stateChange.timestamp))
      }
    }
    else {
      // the incoming update is coming from the currently active tx
      if(isActiveTransmitter(stateChange.getMacAddress())) {
        // is it connected and attached AND is the other ALSO connected and attached
        return if(isConnectedAndAttached(stateChange.getMacAddress()) && otherConnectedAndAttached) {
          wasPreviousUiStateMultipleTx = true
          UpdatedStateChange(StateChange(UiState.MULTIPLE_TRANSMITTERS, stateChange.overlayState, stateChange.bleDevice, timestamp = stateChange.timestamp))
        }
        else {
          wasPreviousUiStateMultipleTx = false
          if(otherConnectedAndAttached) {
            val otherStateChange = latestStateFromOtherTransmitter(stateChange.getMacAddress())!!
            setActiveTransmitter(otherStateChange)
            UpdatedStateChange(otherStateChange, true)
          }
          else {
            UpdatedStateChange(stateChange)
          }
        }
      }
      else {
        if(isConnectedAndAttached(stateChange.getMacAddress())) {
          return if(otherConnectedAndAttached) {
            val latestStateFromOther = getLatestState(activeTransmitterMacAddress())
            wasPreviousUiStateMultipleTx = true
            UpdatedStateChange(StateChange(UiState.MULTIPLE_TRANSMITTERS, stateChange.overlayState, latestStateFromOther!!.bleDevice, timestamp = latestStateFromOther.timestamp))
          }
          else {
            wasPreviousUiStateMultipleTx = false
            setActiveTransmitter(stateChange)
            UpdatedStateChange(stateChange, true)
          }
        }
        else {
          wasPreviousUiStateMultipleTx = false
          val otherStateChange = latestStateFromOtherTransmitter(stateChange.getMacAddress())
          if(otherStateChange != null) {
            return UpdatedStateChange(otherStateChange)
          }

          return null
//          else {
//            Timber.wtf("NOT HERE")
//            // we set the active transmitter
//            // this case might occur when the currently active transmitter is DISCONNECTED
//            setActiveTransmitter(stateChange)
//            UpdatedStateChange(stateChange, true)
//          }
        }
      }
    }
  }

  fun latestDetach() : LatestDetachVector? {
    return latestDetachVector
  }

  fun clearLatestDetach() {
    latestDetachVector = null
    preferences.latestDetach = ""
  }

  private fun hasActiveTransmitter() : Boolean {
    return activeTransmitterMacAddress().isNotBlank()
  }

  /**
   * @return active transmitter macAddress
   */
  private fun activeTransmitterMacAddress() : String {
    return activeTransmitter
  }

  private fun setActiveTransmitter(stateChange: StateChange) {
    Timber.d("new active transmitter: [$activeTransmitter] => [${stateChange.getMacAddress()}]")
    activeTransmitter = stateChange.getMacAddress()

    // We store it here, because the current active TX may have changed from beginning to this place
    preferences.latestStateFromActiveTx = stateChange.serialize()
  }

  private fun addToHistory(stateChange: StateChange) {
    val stack = transmitterStateHistory[stateChange.getMacAddress()] ?: Stack(3)
    stack.push(stateChange)
    transmitterStateHistory[stateChange.getMacAddress()] = stack

    if(isActiveTransmitter(stateChange.getMacAddress()) && stateChange.bleDevice.isDetached()) {
      latestDetachVector = LatestDetachVector(stateChange, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
      preferences.latestDetach = latestDetachVector?.serialize() ?: ""
    }
  }

  private fun isOtherTransmitterAttached(thisTransmitterAddress: String): Boolean {
    val otherTransmitter =
      transmitterStateHistory.entries.firstOrNull { it.key != thisTransmitterAddress }

    otherTransmitter?.let {
      // verify that the *other* transmitter is actually connected
      val isConnected = bluetoothManager.isConnected(it.key)
      return isConnected && it.value.peek()?.bleDevice?.isAttached() ?: false
    }

    return false
  }

  private fun latestStateFromOtherTransmitter(thisTransmitterAddress: String): StateChange? {
    val otherTransmitter =
      transmitterStateHistory.entries.firstOrNull { it.key != thisTransmitterAddress }

    otherTransmitter?.let {
      // verify that the *other* transmitter is actually connected
      val isConnected = bluetoothManager.isConnected(it.key)
      return if(isConnected) it.value.peek() else null
    }

    return null
  }

  private fun isCurrentlyAttached(macAddress: String) : Boolean {
    return isCurrentlyAttached(getLatestState(macAddress))
  }

  private fun isCurrentlyAttached(stateChange: StateChange?) : Boolean {
    return stateChange?.bleDevice?.isAttached() ?: false
  }

  private fun isConnectedAndAttached(macAddress: String) : Boolean {
    val isConnected = bluetoothManager.isConnected(macAddress)
    val latestState = transmitterStateHistory.entries.firstOrNull { it.key == macAddress }?.value?.peek()

    // it's either attached directly or attached outside BLE
    val isAttached = latestState?.bleDevice?.isAttached() ?: false || latestState?.bleDevice?.isAttachedOutsideBLE() ?: false

    return isConnected && isAttached
  }

  fun isOnlyTransmitterAdded(macAddress: String) : Boolean {
    return transmitterStateHistory[macAddress] == null || (transmitterStateHistory[macAddress] != null && transmitterStateHistory.size == 1)
  }

  fun deleteTransmitterHistory(macAddress: String) {
    if(isActiveTransmitter(macAddress)) {
      activeTransmitter = ""
    }

    transmitterStateHistory.remove(macAddress)
  }

  private fun isOtherConnectedAndAttached(thisTransmitterAddress: String) : Boolean {
    val otherTransmitter = transmitterStateHistory.entries.firstOrNull { it.key != thisTransmitterAddress } ?: return false
    return isConnectedAndAttached(otherTransmitter.key)
  }

  /**
   * whether or not the incoming Tx macAddress is the currently active Tx
   */
  fun isActiveTransmitter(macAddress: String): Boolean {
    return activeTransmitter == macAddress
  }

  fun getLatestState(macAddress: String) : StateChange? {
    return transmitterStateHistory[macAddress]?.peek()
  }

  fun previousState(macAddress: String) : StateChange? {
    return transmitterStateHistory[macAddress]?.peekPrevious()
  }

  /**
   * Tell the manager that a Tx Disconnect occurred
   *
   * Will switch active transmitter if needed and return the latest state
   * @return latest State from other transmitter or NULL if we only have one Tx
   */
  fun onTransmitterDisconnect(disconnectedTransmitterMacAddress: String) : StateChange? {
    if(isActiveTransmitter(disconnectedTransmitterMacAddress)) {
      if(isOtherTransmitterAttached(disconnectedTransmitterMacAddress)) {
        return latestStateFromOtherTransmitter(disconnectedTransmitterMacAddress)
      }
      else {
        if(isCurrentlyAttached(disconnectedTransmitterMacAddress)) {
          val latestState = getLatestState(disconnectedTransmitterMacAddress)
          latestState?.let {
            it.bleDevice.connectionState = BleConnectionState.DISCONNECTED
          }

          return latestState
        }
      }
    }
    else {
      if(isCurrentlyAttached(disconnectedTransmitterMacAddress)) {
        return latestStateFromOtherTransmitter(disconnectedTransmitterMacAddress)
      }
      else {
        // this is not the currently active transmitter - nor is it attached - but we still need to update the disconnected state
        val latestState = getLatestState(disconnectedTransmitterMacAddress)

        latestState?.let {
          it.bleDevice.connectionState = BleConnectionState.DISCONNECTED
        }
      }
    }

    return null
  }
}