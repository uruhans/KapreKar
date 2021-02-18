package com.coloplast.olympus.patchsplit

import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.patchsplit.models.ApsAction
import com.coloplast.olympus.patchsplit.models.CycleResult
import com.coloplast.olympus.ui.feature.UpdatedStateChange

interface AutoPatchDetectionEngine {
  fun determineAttachCycle(updatedStateChange: UpdatedStateChange, isDetachVectorValid: Boolean): CycleResult
  fun determineApsAction(cycleResult: CycleResult): ApsAction
  fun resetOrStitchCandidate(apsAction: ApsAction, lastValidDetach: StateChange?) : ByteArray?
}