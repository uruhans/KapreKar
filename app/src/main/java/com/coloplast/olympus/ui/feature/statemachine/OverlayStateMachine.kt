package com.coloplast.olympus.ui.feature.statemachine

import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState

interface OverlayStateMachine {
  fun invoke(newState: OverlayState): OverlayState
}