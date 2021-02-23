package com.coloplast.olympus.bluetooth.model

import com.coloplast.olympus.patchsplit.AutoPatchDetectionEngine
import com.coloplast.olympus.ui.feature.TransmitterManager
import com.coloplast.olympus.ui.feature.UpdatedStateChange

class BluetoothServiceHelper(
  private val transmitterManager: TransmitterManager,
  private val apsEngine: AutoPatchDetectionEngine
) {

  fun handleTransmitterUpdate(stateChange: StateChange) : UpdatedStateChange {
    val updatedStateChange = transmitterManager.determineCurrentlyActiveTransmitterAndReturnStateChangeFromIt(stateChange)
    val lastValidDetach = transmitterManager.latestDetach()
    val previousStatusVector = transmitterManager.previousState(updatedStateChange.stateChange.getMacAddress())
    val cycleResult = apsEngine.determineAttachCycle(updatedStateChange, lastValidDetach?.isValid() ?: false, previousStatusVector?.bleDevice?.isBlocked() ?: false)
    val apsMethod = apsEngine.determineApsMethod(cycleResult)
    val apsAction = apsEngine.determineApsAction(apsMethod)

    updatedStateChange.apsMethod = apsMethod
    updatedStateChange.stateChange.apsAction = apsAction
    updatedStateChange.stateChange.resetOrStitchCandidate = apsEngine.resetOrStitchCandidate(apsAction, lastValidDetach?.stateChange)

    return updatedStateChange
  }
}