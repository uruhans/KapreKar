package com.coloplast.olympus.patchsplit

import com.coloplast.olympus.patchsplit.models.CompromisedVault
import com.coloplast.olympus.patchsplit.models.*
import com.coloplast.olympus.ui.feature.UpdatedStateChange
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.random.Random

class AutoPatchDetectionEngine(private val compromisedVault: CompromisedVault, private val randomizer: Random = Random(10000)) {

//  private val detachedVectorExpirationTime: Long = -60 * 60 // 1 hour
  private val entryExpirationTime: Long = 60 * 60 * 2 // 2 hours
  private val autoSplitInternal: Long = 60 * 60 * 12 // 12 hours
  private var lastSplit: Long? = null

  private var wasPreviousUiStateMultipleTx: Boolean = false

  fun determineAttachCycle(
    updatedStateChange: UpdatedStateChange,
    isDetachVectorValid: Boolean
  ): CycleResult {
    val incomingPackage = updatedStateChange.stateChange
    val incomingCompromised = incomingPackage.bleDevice.peripheral?.transmitter?.compromised

    if (incomingPackage.state != UiState.MULTIPLE_TRANSMITTERS) {
      if (!incomingPackage.bleDevice.isBlocked()) {
        if (updatedStateChange.activeTransmitterChanged) {
          if (wasPreviousUiStateMultipleTx) {
            wasPreviousUiStateMultipleTx = false
            return CycleResult.Success(AttachCycleType.ComingFromMultipleTransmitters(incomingCompromised))
          }
          wasPreviousUiStateMultipleTx = false
          return CycleResult.Success(AttachCycleType.Standard(incomingCompromised))
        }
        else {
          wasPreviousUiStateMultipleTx = false
          return if (isDetachVectorValid && incomingPackage.bleDevice.isAttached()) {
            CycleResult.Success(AttachCycleType.Standard(incomingCompromised))
          }
          else if (incomingPackage.bleDevice.isAttachedOutsideBLE()) {
            CycleResult.Success(AttachCycleType.OutOfRange(incomingCompromised))
          }
          else {
            CycleResult.Failure(AttachCycleError.NoDetach)
          }
        }
      }
      /// We have had a Att. Cycle, but we cannot act on. Do we set a flag that we need to act on once the vector is valid again?
      return CycleResult.Failure(AttachCycleError.Untrusted)
    }

    wasPreviousUiStateMultipleTx = true
    return CycleResult.Failure(AttachCycleError.MultipleTransmittersAttached)
  }

  fun determineApsAction(attachCycleType: AttachCycleType): ApsAction {
    val calculationValues = when (attachCycleType) {
      is AttachCycleType.ComingFromMultipleTransmitters -> {
        // ==> method b if between 2=>12
        CalculationValues(attachCycleType.compromised, compromisedVault.get())
      }
      is AttachCycleType.OutOfRange -> {
        CalculationValues(compromisedVault.getIfValid(entryExpirationTime), attachCycleType.compromised)
      }
      is AttachCycleType.Standard -> {
        CalculationValues(compromisedVault.getIfValid(entryExpirationTime), attachCycleType.compromised)
      }
    }

    if (calculationValues.to == null) {
      return ApsAction.Nothing()
    }

    val action = if (lastSplit != null && LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - lastSplit!! > autoSplitInternal) {
      ApsAction.Split()
    }
    else {
      calculateSplitCommand(calculationValues.from, calculationValues.to)
    }

    if (action is ApsAction.Split) {
      lastSplit = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    }

    return action
  }

  private fun calculateSplitCommand(from: Int?, to: Int): ApsAction {
    if(from != null && from == 0 && to == 0) {
      val random = randomizer.nextInt(0, 1000)
      if(random < 430) {
        // RESET Tx + patch++
        return ApsAction.Split()
      }
      // Reset Tx
      return ApsAction.Reset()
    }

    /// Magic number is coming from Coloplast. For more information see Coloplast documentation on Auto patch detection.
    val intercept = 10.2643

    // METHOD C
    // If the to value is less than 10, that means that the sensor layer in which the transmitter has been placed, is so new that even if a sensor is triggered it should still be reset.
    // This is what "We" call a wet mount.
    if (to < 10) {
      // METHOD D
      return if (to < 1) {
        ApsAction.Split()
      }
      else {
        ApsAction.Reset()
      }
    }
    else {
      if (from != null) {
        // METHOD A
        // For a deeper explanation of the equation please refer to the Coloplast documentation of APS.
        val patchProbability = to.toDouble() - from.toDouble() + intercept
        return if (patchProbability < 0) {
          ApsAction.Split()
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
        return if (to.toDouble() <= 20.44) {
          ApsAction.Split()
        }
        else {
          ApsAction.Stitch()
        }
      }
    }
  }
}