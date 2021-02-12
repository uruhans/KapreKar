package com.coloplast.olympus.patchsplit

import com.coloplast.olympus.patchsplit.models.ApsAction
import com.coloplast.olympus.patchsplit.models.CompromisedVault
import com.coloplast.olympus.ui.feature.UpdatedStateChange
import com.coloplast.olympus.ui.feature.statemachine.models.UiState

class AutoPatchDetectionEngine(private val compromisedVault: CompromisedVault) {

  private val detachedVectorExpirationTime: Long = -60 * 60 // 1 hour
  private val entryExpirationTime: Long = 60 * 60 * 2 // 2 hours
  private val autoSplitInternal: Long = 60 * 60 * 12 // 12 hours

  private var last: Long? = null

  fun determineAttachCycle(updatedStateChange: UpdatedStateChange, isDetachVectorValid: Boolean) : Boolean {
    val incomingPackage = updatedStateChange.stateChange

    if(incomingPackage.state == UiState.MULTIPLE_TRANSMITTERS) {
      if(updatedStateChange.activeTransmitterChanged) {
        return true
      }
      else {
        if(isDetachVectorValid && incomingPackage.bleDevice.isAttached()) {
          return true
        }
        else if(incomingPackage.bleDevice.isAttachedOutsideBLE()) {
          return true
        }
      }
    }
    return false
  }

  fun determineApsAction() : ApsAction {
    val fromEntry = compromisedVault.previous()
    val toEntry = compromisedVault.last()

    if(last != null && last!! > autoSplitInternal) {
      return ApsAction.Reset(newPatch = true)
    }
    else if(toEntry != null && toEntry.timestamp < entryExpirationTime) {
      if(fromEntry != null && fromEntry.timestamp < entryExpirationTime) {
        return calculateSplitCommand(fromEntry.running, toEntry.locked)
      }
    }

    return ApsAction.None()
  }

  private fun calculateSplitCommand(from: Int?, to: Int?): ApsAction {
    if (to == null) {
      return ApsAction.None()
    }

    // Magic number is coming from Coloplast. For more information see Coloplast documentation on Auto patch detection.
    val intercept = 10.2643

    return if (to < 10) {
      // METHOD 0
      // If the to value is less than 10, that means that the sensor layer in which the transmitter has been placed, is so new that even if a sensor is triggered it should still be reset.
      // This is what "We" call a wet mount.
      ApsAction.Reset(newPatch = true)
    }
    else {
      if (from != null) {
        // METHOD A
        // For a deeper explanation of the equation please refer to the Coloplast documentation of APS.
        val patchProbability = to.toDouble() - from.toDouble() + intercept
        if (patchProbability < 0) {
          ApsAction.Reset(newPatch = true)
        }
        else {
          ApsAction.Stitch()
        }
      }
      else {
        // "METHOD B
        // If we for some reason do not have a from value, the command can be calculated using only the to value.
        // This could be because of bluetooth disconnection.
        // This magic number is determined on data analysis and was decided by Coloplast, please refer to the APS Coloplast documentation.
        if (to.toDouble() <= 20.44) {
          ApsAction.Reset(newPatch = true)
        }
        else {
          ApsAction.Stitch()
        }
      }
    }
  }
}