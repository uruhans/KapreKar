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
  PAY_ATTENTION_F,
  WET_MOUNT_G,
  CUTGUARD_BROKEN_I,
  WATER_INGRESS_DETECTION_J,
  CUTGUARD_DIRT_IN_TRANSMITTER_K,
  INNER_BLIND_LEAK_L,
  NO_BATTERY_R,
  TRANSMITTER_DETACHED_V,
  TRANSMITTER_BATTERY_CHARGING_W,
  MULTIPLE_TRANSMITTERS;

  /**
   * return a notificationId based on the UiState
   *
   * - Notice: We are adding a magic number here, because we have multiple enums that can display notifications, and hence the magic number
   *           will make this particular notificationId unique for UiState notifications
   */
  fun notificationId(): Int {
    return this.ordinal + MAGIC_NUMBER
  }
  
  companion object {
    private const val MAGIC_NUMBER = 100
  
    /**
     * Convert notification Id into a UiState
     */
    fun fromNotificationId(notificationId: Int) : UiState {
      return values()[notificationId - MAGIC_NUMBER]
    }

    fun uiStateToUiStateLetter(uiState: UiState): String {
      when(uiState) {
        OK_A -> return "A"
        INNER_BREACH_B -> return "B"
        OUTER_BREACH_C -> return "C"
        INNER_BLIND_D -> return "D"
        TOTAL_BLIND_E -> return "E"
        PAY_ATTENTION_F -> return "F"
        WET_MOUNT_G -> return "G"
        CUTGUARD_BROKEN_I -> return "I"
        WATER_INGRESS_DETECTION_J -> return "J"
        CUTGUARD_DIRT_IN_TRANSMITTER_K -> return "K"
        INNER_BLIND_LEAK_L -> return "L"
        NO_BATTERY_R -> return "R"
        TRANSMITTER_DETACHED_V -> return "V"
        TRANSMITTER_BATTERY_CHARGING_W -> return "W"
        else -> return ""
      }
    }

  }
}