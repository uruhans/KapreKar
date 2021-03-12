package com.coloplast.olympus.ui.feature.statemachine

import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import timber.log.Timber

abstract class BaseUiStateMachine : UiStateMachine {
  
  // the state we are currently in
  private var state: UiState = UiState.INITIAL
  // contains reference to the method that is meant to be run in the current state we are in
  private var currentState: (uiModelState: UiState) -> Boolean = { initial(it) }
  
  override fun currentState(): UiState {
    return invoke(state)
  }
  
  /**
   * the method that is run on every new state coming in
   */
  override fun invoke(newState: UiState): UiState {
    // if we have the same state as before, there is no need to run through the state machine
    if (newState == state) {
      Timber.d("Skipping state - we are already in [$newState]")
      return newState
    }

    /**
     * Update 2021-03-12
     * During APS v2, a lot of stateMachine progressions were missing - and it's not the first time either
     * nor will it be the last - i am forcing a state update here no matter where we come from and go to
     * in order to mitigate a lot of "bugs" being reported
     *
     * I still think the stateMachine is the proper way to go about it, but seeing as requirements keep changing and
     * we keep forgetting to add these steps - i feel its better to go about it this way, and leave the changing of UiState
     * to the transmitter - and not us.
     */
    // invoke the current state with the new state as parameter
    return if (true) {//currentState.invoke(newState)) {
      // the state was handled so update to new state
      Timber.d("Switching to new state: [$state] -> [$newState]")
      updateState(newState)
      newState
    }
    else {
      // log that the state was not handled - for debugging purposes
      // it might not mean there is something wrong
      // but it'll make it easier for devs to determine if something is wrong or not
      Timber.e("State not handled: [$state] -> [$newState]")
      state
    }
  }
  
  /**
   * Update the currentState with the new state
   * it will update the 'currentState' reference to the correct state 'method' to be run on the next state check
   * and then update the state variable so we can do an equals check to see whether or not we even need to go through the statemachine
   */
  private fun updateState(newState: UiState) {
    currentState = when (newState) {
      UiState.INITIAL -> {
        { initial(it) }
      }
      UiState.DISCONNECTED -> {
        { somethingElse(it) }
      }
      UiState.OK_A -> {
        { ok_A(it) }
      }
      UiState.INNER_BREACH_B -> {
        { innerBreach_B(it) }
      }
      UiState.OUTER_BREACH_C -> {
        { outerBreach_C(it) }
      }
      UiState.INNER_BLIND_D -> {
        { innerBlind_D(it) }
      }
      UiState.TOTAL_BLIND_E -> {
        { totalBlind_E(it) }
      }
      UiState.PAY_ATTENTION_F -> {
        { payAttention_F(it) }
      }
      UiState.CUTGUARD_BROKEN_I -> {
        { cutguardBroken_I(it) }
      }
      UiState.WATER_INGRESS_DETECTION_J -> {
        { waterIngressDetection_J(it) }
      }
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K -> {
        { cutguardDirtInTransmitter_K(it) }
      }
      UiState.INNER_BLIND_LEAK_L -> {
        { innerBlindLeak_L(it) }
      }
      UiState.NO_BATTERY_R -> {
        { noBattery_R(it) }
      }
      UiState.TRANSMITTER_DETACHED_V -> {
        { transmitterDetached_V(it) }
      }
      UiState.TRANSMITTER_BATTERY_CHARGING_W -> {
        { transmitterBatteryCharging_W(it) }
      }
      UiState.SOMETHING_ELSE -> {
        { somethingElse(it) }
      }
      else -> {
        throw NullPointerException("State not handled - should be impossible to get here with this state -> [$newState]")
      }
    }
    
    state = newState
  }
  
  override fun reset() {
    state = UiState.INITIAL
  }
}