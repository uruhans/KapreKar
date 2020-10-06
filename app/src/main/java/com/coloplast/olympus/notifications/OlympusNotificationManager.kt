package com.coloplast.olympus.notifications

import com.coloplast.olympus.R
import com.coloplast.olympus.notifications.models.NotificationPayload
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState

/**
 * A manager to send notifications to the user
 * Currently it can send notifications based on either UiState or OverlayState
 *
 * NOTICE: some notifications have not been implemented yet as they have not been implemented in the UI yet
 */
class OlympusNotificationManager(private val notificationCenter: NotificationCenter) {
  
  /**
   * send a notification solely based on a notification id
   * - this will then try to figure out what type of notification it is, and then send the correct type (UiState, OverlayState)
   */
  fun send(notificationId: Int) {
    if(OverlayState.isOverlayState(notificationId)) {
      send(OverlayState.fromNotificationId(notificationId))
    }
    else {
      send(UiState.fromNotificationId(notificationId))
    }
  }
  
  /**
   * send notifications based on current UiState
   */
  fun send(state: UiState) {
    val payload = when (state) {
      UiState.FIRMWARE_AVAILABLE -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_firmware_update_title, title = R.string.lock_notification_firmware_update)
      }
      UiState.APP_UPDATE_AVAILABLE -> { null }
      UiState.NO_CHANGE -> { null }
      UiState.TRANSMITTER_DELETED -> { null }
      UiState.INITIAL -> { null }
      UiState.BLUETOOTH_NOT_ENABLED -> { null }
      UiState.LOCATION_SERVICES_NOT_ENABLED -> { null }
      UiState.CONNECTING -> { null }
      UiState.SCANNING -> { null }
      UiState.DISCONNECTED -> { null }
      UiState.MULTIPLE_TRANSMITTERS -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_multiple_transmitters)
      }
      UiState.TRANSMITTER_NOT_FOUND -> { null }
      UiState.SOMETHING_ELSE,
      UiState.OK_A -> {
        // we will clear all notifications that may be shown - now that everything is OK - we don't need earlier notification warnings
        notificationCenter.clear()
        null
      }
      UiState.INNER_BREACH_B -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_inner)
      }
      UiState.OUTER_BREACH_C -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_outer)
      }
      UiState.CUTGUARD_BROKEN_I -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_broken)
      }
      UiState.INNER_BLIND_D -> { null }
      UiState.TOTAL_BLIND_E -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_total_blind)
      }
      UiState.PAY_ATTENTION_F -> { null }
      UiState.WET_MOUNT_G -> { null }
      UiState.WATER_INGRESS_DETECTION_J -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_water)
      }
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_dirt)
      }
      UiState.NO_BATTERY_R -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_disconnection_battery)
      }
      UiState.TRANSMITTER_DETACHED_V -> { null }
      UiState.TRANSMITTER_BATTERY_CHARGING_W -> { null }
    }
    
    payload?.let {
      notificationCenter.show(it)
    }
  }
  
  /**
   * Send notification based on current OverlayState
   */
  fun send(state: OverlayState) {
    val payload = when(state) {
      OverlayState.NONE -> { null }
      OverlayState.BATTERY_LOW -> {
        
        // this is temporary and not *real* code - but here to present a way to do overlay state notifications
        // TODO - change when we have properly implemented overlay states
        NotificationPayload(state.notificationId(), R.string.lock_notification_battery_critical)
      }
      OverlayState.QUESTION -> { null }
      OverlayState.BLE_CONNECTION_LOST -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_ble_disconnected)
      }
    }
  
    payload?.let {
      notificationCenter.show(it)
    }
  }
  
  /**
   * cancel (remove) a specific notification
   */
  fun cancel(notificationId: Int) {
    notificationCenter.cancel(notificationId)
  }
}