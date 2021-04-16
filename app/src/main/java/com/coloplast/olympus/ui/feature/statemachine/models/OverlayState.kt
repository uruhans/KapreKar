package com.coloplast.olympus.ui.feature.statemachine.models

enum class OverlayState {
  NONE,
  BATTERY_LOW,
  BLE_CONNECTION_LOST;

  /**
   * return a notificationId based on the overlaystate
   *
   * - Notice: We are adding a magic number here, because we have multiple enums that can display notifications, and hence the magic number
   *           will make this particular notificationId unique for OverlayState notifications
   */
  fun notificationId(): Int {
    return this.ordinal + MAGIC_NUMBER
  }

  fun toLetter() : String {
    return when(this) {
      BATTERY_LOW -> "U"
      else -> ""
    }
  }

  companion object {
    private const val MAGIC_NUMBER = 500
  
    /**
     * Convert notification Id into an OverlayState
     */
    fun fromNotificationId(notificationId: Int) : OverlayState {
      return values()[notificationId - MAGIC_NUMBER]
    }
  
    /**
     * try to determine whether or not the notificationId coming in is an OverlayState notificationId
     */
    fun isOverlayState(notificationId: Int) : Boolean {
      return notificationId.toFloat() / MAGIC_NUMBER > 1f
    }
  }
}