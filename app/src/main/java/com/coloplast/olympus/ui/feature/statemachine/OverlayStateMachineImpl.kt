package com.coloplast.olympus.ui.feature.statemachine

import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState

class OverlayStateMachineImpl : OverlayStateMachine {
  
  private var state: OverlayState = OverlayState.NONE
  
  override fun invoke(newState: OverlayState): OverlayState {
    if (newState == state) {
      // we check if the last state is the same as the new state
      // if this is the case we change it to NONE and return it
      // this is to prevent multiple overlays appearing or showing the same overlay again
      return OverlayState.NONE
    }
    
    state = newState
    return newState
  }
}