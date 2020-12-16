package com.coloplast.olympus.ui.feature

import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.services.OlympusWirelessProtocol
import com.coloplast.olympus.bluetooth.model.PatchProbability
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.shared.moshi.deserialize
import com.coloplast.olympus.shared.moshi.serialize
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.Stack
import com.coloplast.olympus.ui.feature.home.models.Stitch
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import timber.log.Timber
import java.time.Instant

/**
 * This manager determines which Tx is currently the *active* one (ie: the one whose state is being displayed on the status screen)
 */
class TransmitterManager(
  private val preferences: Preferences,
  private val bluetoothManager: BluetoothManager
) {

  private val transmitterStateHistory = mutableMapOf<String, Stack<StateChange>>()
  private var activeTransmitter
    get() = preferences.activeTransmitter
    set(value) {
      preferences.activeTransmitter = value
    }

  private var wasPreviousUiStateMultipleTx = false

  // constants for automatic patch detection
  private val validDetachInterval = 60*60

  init {
    preferences.latestStateFromActiveTx.let { latestStateFromActiveTx ->
      if(latestStateFromActiveTx.isEmpty()) {
        return@let
      }

      latestStateFromActiveTx.deserialize<StateChange>()?.let { stateChange ->
        transmitterStateHistory[activeTransmitter] = Stack<StateChange>(5).also {
          it.push(stateChange)
        }
      }

      transmitterStateHistory.remove("")
    }
  }

  /**
   * Determine the currently active transmitter and returns the latest stateChange from said active transmitter
   *  - Stores the stateChange for later use
   *  - Incoming stateChange might not be from the same transmitter as the one being returned
   */
  fun determineCurrentlyActiveTransmitterAndReturnStateChangeFromIt(stateChange: StateChange): UpdatedStateChange {
    addToHistory(stateChange)

    wasPreviousUiStateMultipleTx = false

    val otherConnectedAndAttached = isOtherConnectedAndAttached(stateChange.getMacAddress())

    if(!hasActiveTransmitter()) {
      return if(isCurrentlyAttached(stateChange)) {
        // change active tx to this one
        setActiveTransmitter(stateChange)
        // return it's state
        UpdatedStateChange(stateChange, true)
      }
      else {
        // we have no active tx - nor is the current on attached - force UiState DETACHED
        UpdatedStateChange(StateChange(UiState.TRANSMITTER_DETACHED_V, stateChange.overlayState, stateChange.bleDevice, stateChange.patchProbability, stateChange.timestamp))
      }
    }
    else {
      // the incoming update is coming from the currently active tx
      if(isActiveTransmitter(stateChange.getMacAddress())) {
        // is it connected and attached AND is the other ALSO connected and attached
        return if(isConnectedAndAttached(stateChange.getMacAddress()) && otherConnectedAndAttached) {
          wasPreviousUiStateMultipleTx = true
          UpdatedStateChange(StateChange(UiState.MULTIPLE_TRANSMITTERS, stateChange.overlayState, stateChange.bleDevice, stateChange.patchProbability, stateChange.timestamp))
        }
        else {
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
            wasPreviousUiStateMultipleTx = true
            UpdatedStateChange(StateChange(UiState.MULTIPLE_TRANSMITTERS, stateChange.overlayState, stateChange.bleDevice, stateChange.patchProbability, stateChange.timestamp))
          }
          else {
            setActiveTransmitter(stateChange)
            UpdatedStateChange(stateChange, true)
          }
        }
        else {
          val otherStateChange = latestStateFromOtherTransmitter(stateChange.getMacAddress())
          return if(otherStateChange != null) {
            UpdatedStateChange(otherStateChange)
          }
          else {
            UpdatedStateChange(stateChange)
          }
        }
      }
    }
  }

  private fun lastValidDetachState(macAddress: String) : StateChange? {
    val validTimeStamp = Instant.now().epochSecond - validDetachInterval
    val latestTxHistory = transmitterStateHistory.entries.firstOrNull { it.key == macAddress }?.value
    return latestTxHistory?.firstOrNull { it.state == UiState.TRANSMITTER_DETACHED_V && it.timestamp > validTimeStamp }
  }

  fun determinePatchCommand(updatedStateChange: UpdatedStateChange) {
    var patchProbability = PatchProbability.NOT_SET
    val stateChange = updatedStateChange.stateChange

    val incomingCompromised = stateChange.bleDevice.peripheral?.transmitter?.compromised

    if(stateChange.state != UiState.MULTIPLE_TRANSMITTERS && stateChange.bleDevice.isBlocked()) {
      if(updatedStateChange.activeTransmitterChanged) {
        val otherStateChange = latestStateFromOtherTransmitter(updatedStateChange.stateChange.getMacAddress())

        if(wasPreviousUiStateMultipleTx) {
          val otherCompromised = otherStateChange?.bleDevice?.peripheral?.transmitter?.compromised
          val from = if(stateChange.bleDevice.isBlocked()) incomingCompromised else null

          if(otherStateChange?.bleDevice?.isBlocked() == false) {
            patchProbability = calculateSplitCommand(from, otherCompromised)
          }
        }
        else {
          if(otherStateChange?.bleDevice?.isBlocked() == false) {
            val lastOtherValidDetachState = lastValidDetachState(otherStateChange.getMacAddress())

            val from = if(lastOtherValidDetachState?.bleDevice?.isBlocked() != false) lastOtherValidDetachState?.bleDevice?.peripheral?.transmitter?.compromised else null
            patchProbability = calculateSplitCommand(from, incomingCompromised)
          }
        }
      }
      else {
        val lastValidDetachState = lastValidDetachState(stateChange.getMacAddress())

        if(stateChange.bleDevice.isAttached() && !stateChange.bleDevice.isBlocked() && lastValidDetachState != null) {
          val from = if(lastValidDetachState.bleDevice.isBlocked()) lastValidDetachState.bleDevice.peripheral?.transmitter?.compromised else null
          patchProbability = calculateSplitCommand(from, incomingCompromised)
        }
        else if(stateChange.bleDevice.isAttachedOutsideBLE()) {
          patchProbability = calculateSplitCommand(null, incomingCompromised)
        }
      }
    }

    updatedStateChange.stateChange.patchProbability = patchProbability
  }

  private fun calculateSplitCommand(from: Int?, to: Int?) : PatchProbability {
    if(to == null) {
      Timber.d("calculateSplitCommand: to is null")
      return PatchProbability.NOT_SET
    }

    // Magic number is coming from Coloplast. For more information see Coloplast documentation on Auto patch detection.
    val intercept = 10.2643

    return if(to < 10) {
      // METHOD 0
      // If the to value is less than 10, that means that the sensor layer in which the transmitter has been placed, is so new that even if a sensor is triggered it should still be reset.
      // This is what "We" call a wet mount.
      Timber.d("calculateSplitCommand: to < 10")
      PatchProbability.NEW
    }
    else {
      if(from != null) {
        // METHOD A
        // For a deeper explanation of the equation please refer to the Coloplast documentation of APS.
       val patchProbability = to.toDouble() - from.toDouble() + intercept
        if(patchProbability < 0) {
          Timber.d("calculateSplitCommand: patchProbability < 0")
          PatchProbability.NEW
        }
        else {
          Timber.d("calculateSplitCommand: patchProbability >= 0")
          PatchProbability.SAME
        }
      }
      else {
        // "METHOD B
        // If we for some reason do not have a from value, the command can be calculated using only the to value.
        // This could be because of bluetooth disconnection.
        // This magic number is determined on data analysis and was decided by Coloplast, please refer to the APS Coloplast documentation.
        if(to.toDouble() <= 20.44) {
          Timber.d("calculateSplitCommand: to <= 20.44")
          PatchProbability.NEW
        }
        else {
          Timber.d("calculateSplitCommand: to > 20.44")
          PatchProbability.SAME
        }
      }
    }
  }

  private fun hasActiveTransmitter() : Boolean {
    return activeTransmitterMacAddress().isNotBlank()
  }

  /**
   * @return active transmitter macAddress
   */
  fun activeTransmitterMacAddress() : String {
    return activeTransmitter
  }

  /**
   * get stitch bytes from transmitter (used when changing Tx onto the same patch)
   *  - the other Tx being the one that is *not* active
   *  - if we do not have an 'other' transmitter - we return the stitch array from the same transmitter
   *  @return a Stitch object containing the actual stitching or a "Reset" package instead (first time the user is asked both answers do a reset)
   */
  fun getStitchBytes() : Stitch {
    // see if we have the other transmitter
    val otherTxState = latestStateFromOtherTransmitter(activeTransmitter)

    // if we dont have the other transmitter take the latest from the active
    val state = otherTxState ?: transmitterStateHistory.entries.firstOrNull { it.key == activeTransmitter }?.value?.peek()

    return Stitch(
      activeTransmitter,
      state?.bleDevice?.peripheral?.getStitchArray() ?: OlympusWirelessProtocol.Characteristics.RESET_STATUS_DATA)
  }

  private fun setActiveTransmitter(stateChange: StateChange) {
    Timber.d("new active transmitter: [$activeTransmitter] => [${stateChange.getMacAddress()}]")
    activeTransmitter = stateChange.getMacAddress()

    // We store it here, because the current active TX may have changed from beginning to this place
    preferences.latestStateFromActiveTx = stateChange.serialize()
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

  private fun isCurrentlyAttached(macAddress: String) : Boolean {
    return isCurrentlyAttached(getLatestState(macAddress))
  }

  private fun isCurrentlyAttached(stateChange: StateChange?) : Boolean {
    return stateChange?.bleDevice?.isAttached() ?: false
  }

  private fun isConnectedAndAttached(macAddress: String) : Boolean {
    val isConnected = bluetoothManager.bleConnectionStateObservable(macAddress).blockingFirst() == BleConnectionState.CONNECTED
    val latestState = transmitterStateHistory.entries.firstOrNull { it.key == macAddress }?.value?.peek()

    // it's either attached directly or attached outside BLE
    val isAttached = latestState?.bleDevice?.isAttached() ?: false || latestState?.bleDevice?.isAttachedOutsideBLE() ?: false

    return isConnected && isAttached
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

  /**
   * Tell the manager that a Tx Disconnect occurred
   *
   * Will switch active transmitter if needed and return the latest state
   * @return latest State from other transmitter or NULL if we only have one Tx
   */
  fun onTransmitterDisconnect(disconnectedTransmitterMacAddress: String) : StateChange? {
    // if the disconnected tx is currently the active tx
    if(isActiveTransmitter(disconnectedTransmitterMacAddress)) {
      // if the other tx is attached
      if(isOtherTransmitterAttached(disconnectedTransmitterMacAddress)) {
        // get the other tx latest state
        val otherTxStateChange = latestStateFromOtherTransmitter(disconnectedTransmitterMacAddress)
        // if we have another state
        if(otherTxStateChange != null) {
          // return it
          return otherTxStateChange
        }
      }
    }
    else {
      // the disconnected tx is *not* the active one,
      // but see if it *was* attached immediately before disconnect
      if(isCurrentlyAttached(disconnectedTransmitterMacAddress)) {
        // get the latest state from the other tx
        val otherTxStateChange = latestStateFromOtherTransmitter(disconnectedTransmitterMacAddress)
        // if there is another state
        if(otherTxStateChange != null) {
          //return it
          return otherTxStateChange
        }
      }
    }

    return null
  }
}