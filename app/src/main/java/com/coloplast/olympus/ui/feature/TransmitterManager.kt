package com.coloplast.olympus.ui.feature

import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.Attached
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.model.PatchProbability
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.shared.moshi.deserialize
import com.coloplast.olympus.shared.moshi.serialize
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.Stack
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
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
        transmitterStateHistory[activeTransmitter] = Stack<StateChange>(3).also {
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

  private fun getStitchOrResetArray(patchProbability: PatchProbability, stateChange: StateChange): ByteArray? {
    return if(patchProbability == PatchProbability.SAME) {
       stateChange.bleDevice.peripheral?.getStitchArray()
    }
    else {
      byteArrayOf(
        // sensors
        -1, -1, -1, -1, -1, -1,
        // wid
        -1,
        // attached
        Attached.ATTACHED_ON_PATCH.id.toByte(),
        // patchType
        -1,
        // compromised
        -1,
        // rotational angle
        -1,
        // batt pwr
        -1,
        // firmware warning/error
        0, 0)
    }
  }

  fun determinePatchCommand(updatedStateChange: UpdatedStateChange) {
    var patchProbability = PatchProbability.NOT_SET
    val incomingPackage = updatedStateChange.stateChange

    val incomingCompromised = incomingPackage.bleDevice.peripheral?.transmitter?.compromised
    val otherPackage = latestStateFromOtherTransmitter(incomingPackage.getMacAddress())

    var packageToGetStitchArrayFrom = incomingPackage

    if(incomingPackage.state != UiState.MULTIPLE_TRANSMITTERS && !incomingPackage.bleDevice.isBlocked()) {
      if(updatedStateChange.activeTransmitterChanged) {

        if(wasPreviousUiStateMultipleTx) {
          val otherCompromised = otherPackage?.bleDevice?.peripheral?.transmitter?.compromised
          val from = if(incomingPackage.bleDevice.isBlocked()) null else incomingCompromised

          if(otherPackage?.bleDevice?.isBlocked() == false) {
            patchProbability = calculateSplitCommand(from, otherCompromised)
            packageToGetStitchArrayFrom = otherPackage
          }
        }
        else {
          if(!incomingPackage.bleDevice.isBlocked()) {
            val lastOtherValidDetachState = lastValidDetachState(otherPackage?.getMacAddress() ?: "")

            val blocked = lastOtherValidDetachState?.bleDevice?.isBlocked() ?: true
            val from = if(blocked) null else lastOtherValidDetachState?.bleDevice?.peripheral?.transmitter?.compromised
            patchProbability = calculateSplitCommand(from, incomingCompromised)
          }
        }
      }
      else {
        val lastValidDetachState = lastValidDetachState(incomingPackage.getMacAddress())

        if(incomingPackage.bleDevice.isAttached() && !incomingPackage.bleDevice.isBlocked() && lastValidDetachState != null) {
          val from = if(lastValidDetachState.bleDevice.isBlocked()) null else lastValidDetachState.bleDevice.peripheral?.transmitter?.compromised
          patchProbability = calculateSplitCommand(from, incomingCompromised)
        }
        else if(incomingPackage.bleDevice.isAttachedOutsideBLE()) {
          patchProbability = calculateSplitCommand(null, incomingCompromised)
        }
      }
    }

    updatedStateChange.stateChange.patchProbability = patchProbability
    updatedStateChange.stateChange.resetOrStitchCandidate = getStitchOrResetArray(patchProbability, packageToGetStitchArrayFrom)
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
    }

    return null
  }
}