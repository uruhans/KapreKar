package com.coloplast.olympus.ui.feature.statemachine.models

enum class UiState {
  NO_CHANGE,
  INITIAL,
  BLUETOOTH_NOT_ENABLED,
  LOCATION_SERVICES_NOT_ENABLED,
  CONNECTING,
  SCANNING,
  SOMETHING_ELSE,
  DISCONNECTED,
  TRANSMITTER_NOT_FOUND,
  TRANSMITTER_DELETED,
  TRANSMITTERS_EMPTY,
  FIRMWARE_AVAILABLE,
  APP_UPDATE_AVAILABLE,
  SHOW_WELCOME,

  OK_A,
  INNER_BREACH_B,
  OUTER_BREACH_C,
  INNER_BLIND_D,
  TOTAL_BLIND_E,
  PAY_ATTENTION_F2,
  PAY_ATTENTION_F3,
  WET_MOUNT_G,
  CUTGUARD_BROKEN_I,
  WATER_INGRESS_DETECTION_J,
  CUTGUARD_DIRT_IN_TRANSMITTER_K,
  INNER_BLIND_LEAK_L,
  NO_BATTERY_R,
  TRANSMITTER_DETACHED_V,
  TRANSMITTER_BATTERY_CHARGING_W,
  MULTIPLE_TRANSMITTERS,
  WET_MOUNT_X;

  /**
   * return a notificationId based on the UiState
   *
   * - Notice: We are adding a magic number here, because we have multiple enums that can display notifications, and hence the magic number
   *           will make this particular notificationId unique for UiState notifications
   */
  fun notificationId(): Int {
    return this.ordinal + MAGIC_NUMBER
  }

  fun toLetter() : String {
    return when(this) {
      OK_A -> "A"
      INNER_BREACH_B -> "B"
      OUTER_BREACH_C -> "C"
      INNER_BLIND_D -> "D"
      TOTAL_BLIND_E -> "E"
      PAY_ATTENTION_F2 -> "F2"
      PAY_ATTENTION_F3 -> "F3"
      WET_MOUNT_G -> "G"
      CUTGUARD_BROKEN_I -> "I"
      WATER_INGRESS_DETECTION_J -> "J"
      CUTGUARD_DIRT_IN_TRANSMITTER_K -> "K"
      INNER_BLIND_LEAK_L -> "L"
      DISCONNECTED -> "N"
      BLUETOOTH_NOT_ENABLED -> "O"
      TRANSMITTER_NOT_FOUND -> "P"
      NO_BATTERY_R -> "R"
      TRANSMITTER_DETACHED_V -> "V"
      TRANSMITTER_BATTERY_CHARGING_W -> "W"
      TRANSMITTERS_EMPTY -> "Z"
      WET_MOUNT_X -> "X"
      else -> ""
    }
  }

  companion object {
    private const val MAGIC_NUMBER = 100
  
    /**
     * Convert notification Id into a UiState
     */
    fun fromNotificationId(notificationId: Int) : UiState {
      return values()[notificationId - MAGIC_NUMBER]
    }
  }
}