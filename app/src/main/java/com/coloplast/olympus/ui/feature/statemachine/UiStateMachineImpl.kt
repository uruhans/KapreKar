package com.coloplast.olympus.ui.feature.statemachine

import com.coloplast.olympus.ui.feature.statemachine.models.UiState

/**
 * This UiStateMachine determines *where* we can go next.
 *
 * When a new state is coming in, the referenced method (which is the state we are currently in - ie: ok_A) will be hit.
 *  - this method then determines where we can go from here by the *when* statement
 *  - add or remove states to *when* statements to either support or remote state transitions from a given state
 */
class UiStateMachineImpl : BaseUiStateMachine() {

  override fun initial(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.OK_A,
      UiState.INNER_BREACH_B,
      UiState.OUTER_BREACH_C,
      UiState.INNER_BLIND_D,
      UiState.TOTAL_BLIND_E,
      UiState.PAY_ATTENTION_F,
      UiState.WET_MOUNT_G,
      UiState.CUTGUARD_BROKEN_I,
      UiState.WATER_INGRESS_DETECTION_J,
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K,
      UiState.INNER_BLIND_LEAK_L,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.TRANSMITTER_BATTERY_CHARGING_W,
      UiState.DISCONNECTED,
      UiState.SOMETHING_ELSE -> {
        true
      }
      else -> false
    }
  }

  override fun somethingElse(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.OK_A,
      UiState.INNER_BREACH_B,
      UiState.OUTER_BREACH_C,
      UiState.INNER_BLIND_D,
      UiState.TOTAL_BLIND_E,
      UiState.PAY_ATTENTION_F,
      UiState.CUTGUARD_BROKEN_I,
      UiState.WATER_INGRESS_DETECTION_J,
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.TRANSMITTER_BATTERY_CHARGING_W,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }

  override fun ok_A(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.SOMETHING_ELSE,
      UiState.INNER_BREACH_B,
      UiState.OUTER_BREACH_C,
      UiState.INNER_BLIND_D,
      UiState.TOTAL_BLIND_E,
      UiState.PAY_ATTENTION_F,
      UiState.CUTGUARD_BROKEN_I,
      UiState.WATER_INGRESS_DETECTION_J,
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.TRANSMITTER_BATTERY_CHARGING_W,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }

  override fun innerBreach_B(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.SOMETHING_ELSE,
      UiState.OK_A,
      UiState.OUTER_BREACH_C,
      UiState.PAY_ATTENTION_F,
      UiState.CUTGUARD_BROKEN_I,
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K,
      UiState.TRANSMITTER_BATTERY_CHARGING_W,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }

  override fun outerBreach_C(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.SOMETHING_ELSE,
      UiState.OK_A,
      UiState.INNER_BREACH_B,
      UiState.CUTGUARD_BROKEN_I,
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.TRANSMITTER_BATTERY_CHARGING_W,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }

  override fun innerBlind_D(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.SOMETHING_ELSE,
      UiState.OK_A,
      UiState.INNER_BREACH_B,
      UiState.OUTER_BREACH_C,
      UiState.TOTAL_BLIND_E,
      UiState.PAY_ATTENTION_F,
      UiState.CUTGUARD_BROKEN_I,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K,
      UiState.INNER_BLIND_LEAK_L,
      UiState.TRANSMITTER_BATTERY_CHARGING_W,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }

  override fun totalBlind_E(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.SOMETHING_ELSE,
      UiState.OK_A,
      UiState.INNER_BREACH_B,
      UiState.OUTER_BREACH_C,
      UiState.PAY_ATTENTION_F,
      UiState.CUTGUARD_BROKEN_I,
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }

  override fun payAttention_F(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.SOMETHING_ELSE,
      UiState.OK_A,
      UiState.INNER_BREACH_B,
      UiState.OUTER_BREACH_C,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.CUTGUARD_BROKEN_I,
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }

  override fun cutguardBroken_I(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.SOMETHING_ELSE,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.OK_A,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }

  override fun waterIngressDetection_J(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.SOMETHING_ELSE,
      UiState.TRANSMITTER_BATTERY_CHARGING_W,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.CUTGUARD_BROKEN_I,
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K,
      UiState.OK_A,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }

  override fun cutguardDirtInTransmitter_K(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.SOMETHING_ELSE,
      UiState.OK_A,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.CUTGUARD_BROKEN_I,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }

  override fun innerBlindLeak_L(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.SOMETHING_ELSE,
      UiState.OK_A,
      UiState.INNER_BREACH_B,
      UiState.OUTER_BREACH_C,
      UiState.TOTAL_BLIND_E,
      UiState.PAY_ATTENTION_F,
      UiState.CUTGUARD_BROKEN_I,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K,
      UiState.TRANSMITTER_BATTERY_CHARGING_W,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }

  override fun transmitterDetached_V(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.SOMETHING_ELSE,
      UiState.OK_A,
      UiState.INNER_BREACH_B,
      UiState.OUTER_BREACH_C,
      UiState.TOTAL_BLIND_E,
      UiState.INNER_BLIND_D,
      UiState.CUTGUARD_BROKEN_I,
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K,
      UiState.WATER_INGRESS_DETECTION_J,
      UiState.TRANSMITTER_BATTERY_CHARGING_W,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }

  override fun transmitterBatteryCharging_W(newState: UiState): Boolean {
    return when (newState) {
      UiState.BLUETOOTH_NOT_ENABLED,
      UiState.SOMETHING_ELSE,
      UiState.OK_A,
      UiState.TRANSMITTER_DETACHED_V,
      UiState.DISCONNECTED -> {
        true
      }
      else -> false
    }
  }
}