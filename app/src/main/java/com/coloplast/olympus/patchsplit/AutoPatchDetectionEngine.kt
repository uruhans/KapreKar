package com.coloplast.olympus.patchsplit

import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.patchsplit.models.ApsAction
import com.coloplast.olympus.patchsplit.models.ApsMethod
import com.coloplast.olympus.patchsplit.models.CycleResult
import com.coloplast.olympus.ui.feature.UpdatedStateChange

interface AutoPatchDetectionEngine {
  fun determineAttachCycle(updatedStateChange: UpdatedStateChange, isDetachVectorValid: Boolean, previouslyInWidOrCutguard: Boolean): CycleResult
  fun determineApsMethod(cycleResult: CycleResult): ApsMethod
  fun determineApsAction(apsMethod: ApsMethod): ApsAction
  fun resetOrStitchCandidate(apsAction: ApsAction, lastValidDetach: StateChange?) : ByteArray?
}