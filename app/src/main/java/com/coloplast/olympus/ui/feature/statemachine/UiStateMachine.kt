package com.coloplast.olympus.ui.feature.statemachine

import com.coloplast.olympus.ui.feature.statemachine.models.UiState

interface UiStateMachine {
  
  fun currentState() : UiState
  
  fun invoke(newState: UiState) : UiState
  fun reset()
  
  fun initial(newState: UiState): Boolean
  fun somethingElse(newState: UiState): Boolean
  fun ok_A(newState: UiState): Boolean
  fun innerBreach_B(newState: UiState): Boolean
  fun outerBreach_C(newState: UiState): Boolean
  fun innerBlind_D(newState: UiState): Boolean
  fun totalBlind_E(newState: UiState): Boolean
  fun payAttention_F(newState: UiState): Boolean
  fun cutguardBroken_I(newState: UiState): Boolean
  fun waterIngressDetection_J(newState: UiState): Boolean
  fun cutguardDirtInTransmitter_K(newState: UiState): Boolean
  fun innerBlindLeak_L(newState: UiState): Boolean
  fun transmitterDetached_V(newState: UiState): Boolean
  fun transmitterBatteryCharging_W(newState: UiState): Boolean
  
}