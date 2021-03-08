package com.coloplast.olympus.patchsplit

import com.coloplast.olympus.bluetooth.businesslayer.enums.Attached
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.patchsplit.models.*
import com.coloplast.olympus.ui.feature.UpdatedStateChange
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import kotlin.random.Random

class AutoPatchDetectionEngineImpl(private val compromisedVault: CompromisedVault, private val randomizer: Random = Random(10000)) : AutoPatchDetectionEngine {

  private val autoSplitInternal: Long = 60 * 60 * 12 // 12 hours
  private var wasPreviousUiStateMultipleTx: Boolean = false

  override fun determineAttachCycle(
    updatedStateChange: UpdatedStateChange,
    isDetachVectorValid: Boolean,
    previouslyInWidOrCutguard: Boolean
  ): CycleResult {
    val incomingPackage = updatedStateChange.stateChange
    val incomingCompromised = incomingPackage.bleDevice.peripheral?.transmitter?.compromised

    if(incomingPackage.bleDevice.isBlocked() || incomingCompromised == null) {
      return CycleResult.Failure(AttachCycleError.Untrusted)
    }

    if (incomingPackage.state == UiState.MULTIPLE_TRANSMITTERS) {
      wasPreviousUiStateMultipleTx = true
      return CycleResult.Failure(AttachCycleError.MultipleTransmittersAttached)
    }

    if (updatedStateChange.activeTransmitterChanged) {
      if (wasPreviousUiStateMultipleTx) {
        wasPreviousUiStateMultipleTx = false
        return CycleResult.Success(AttachCycleType.ComingFromMultipleWithTxChange())
      }
      return CycleResult.Success(AttachCycleType.Standard(incomingCompromised))
    }
    else {
      // previouslyBlocked = maybe previous vector was in WID or CUTGUARD - but magically returned to other state
      return if ((isDetachVectorValid || previouslyInWidOrCutguard) && incomingPackage.bleDevice.isAttached()) {
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

  override fun determineApsMethod(cycleResult: CycleResult): ApsMethod {
    if(cycleResult is CycleResult.Failure) {
      return ApsMethod.D()
    }

    return when (val attachCycleType = (cycleResult as CycleResult.Success).attachCycleType) {
      is AttachCycleType.ComingFromMultipleWithTxChange -> {
        ApsMethod.C()
      }
      is AttachCycleType.OutOfRange -> {
        determineApsMethod(attachCycleType.compromised)
      }
      is AttachCycleType.Standard -> {
        determineApsMethod(attachCycleType.compromised)
      }
    }
  }

  private fun determineApsMethod(incomingCompromised: Int) : ApsMethod {
    val vaultTimeSinceLastUpdate = compromisedVault.getTimeSinceLast()

    if(vaultTimeSinceLastUpdate > autoSplitInternal) return ApsMethod.C()

    val runningCompromised = compromisedVault.getRunningCompromised()
    return if(runningCompromised != null) {
      ApsMethod.A(runningCompromised, incomingCompromised)
    }
    else {
      ApsMethod.B(incomingCompromised)
    }
  }

  override fun determineApsAction(apsMethod: ApsMethod): ApsAction {
    when(apsMethod) {
      is ApsMethod.A -> {
        val from = apsMethod.from
        val to = apsMethod.to

        if(from == 0 && to == 0) {
          val random = randomizer.nextInt(0, 1000)
          return if(random < 430) ApsAction.Split else ApsAction.Reset
        }

        if (to < 10) {
          return if (to < 1) {
            ApsAction.Split
          }
          else {
            ApsAction.Reset
          }
        }

        /// Magic number is coming from Coloplast. For more information see Coloplast documentation on Auto patch detection.
        val intercept = 10.2643
        val patchProbability = to.toDouble() - from.toDouble() + intercept
        return if (patchProbability < 0) {
          ApsAction.Split
        }
        else {
          ApsAction.Stitch
        }

      }
      is ApsMethod.B -> {
        // If we for some reason do not have a from value, the command can be calculated using only the to value.
        // This could be because of bluetooth disconnection.
        // This magic number is determined on data analysis and was decided by Coloplast, please refer to the APS Coloplast documentation.
        return if (apsMethod.to.toDouble() <= 20.44) {
          ApsAction.Split
        }
        else {
          ApsAction.Stitch
        }
      }
      is ApsMethod.C -> {
        return ApsAction.Split
      }
      is ApsMethod.D -> {
        return ApsAction.Nothing
      }
    }
  }

  override fun resetOrStitchCandidate(apsAction: ApsAction, lastValidDetach: StateChange?): ByteArray? {
    if(apsAction == ApsAction.Stitch && lastValidDetach != null) {
      return stitchArray(lastValidDetach)
    }
    else if(apsAction == ApsAction.Split || apsAction == ApsAction.Reset) {
      return resetArray()
    }

    return null
  }

  private fun resetArray() : ByteArray {
    return byteArrayOf(
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

  private fun stitchArray(stateChange: StateChange) : ByteArray {
    return stateChange.bleDevice.peripheral?.getStitchArray() ?: byteArrayOf(0)
  }
}