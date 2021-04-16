package com.coloplast.olympus.notifications

import com.coloplast.olympus.R
import com.coloplast.olympus.notifications.models.NotificationPayload
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import java.time.Instant

/**
 * A manager to send notifications to the user
 * Currently it can send notifications based on either UiState or OverlayState
 *
 * NOTICE: some notifications have not been implemented yet as they have not been implemented in the UI yet
 */
class OlympusNotificationManager(private val notificationCenter: NotificationCenter) {

  private val latestState = mutableMapOf<String, Int>()
  private var lastNotification: Instant? = null

  /**
   * send a notification solely based on a notification id
   * - this will then try to figure out what type of notification it is, and then send the correct type (UiState, OverlayState)
   */
  fun send(macAddress: String, notificationId: Int) {
    if(OverlayState.isOverlayState(notificationId)) {
      send(macAddress, OverlayState.fromNotificationId(notificationId))
    }
    else {
      send(macAddress, UiState.fromNotificationId(notificationId))
    }
  }

  /**
   * send notifications based on current UiState
   */
  fun send(macAddress: String, state: UiState) {
    // this will stop sending multiple notifications of same type immediately right after
    if(latestState[macAddress] == state.notificationId()) {
      return
    }

    if(macAddress.isNotEmpty()) {
      latestState[macAddress] = state.notificationId()
    }

    val payload = when (state) {
      UiState.FIRMWARE_AVAILABLE -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_firmware_update, title = R.string.lock_notification_firmware_update_title)
      }
      UiState.APP_UPDATE_AVAILABLE -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_app_update, title = R.string.lock_notification_app_update_title)
      }
      UiState.NO_CHANGE -> { null }
      UiState.TRANSMITTER_DELETED -> { null }
      UiState.INITIAL -> { null }
      UiState.BLUETOOTH_NOT_ENABLED -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_ble_off)
      }
      UiState.LOCATION_SERVICES_NOT_ENABLED -> { null }
      UiState.CONNECTING -> {
        NotificationPayload(notificationsToRemove = listOf(
            UiState.BLUETOOTH_NOT_ENABLED.notificationId(),
            UiState.NO_BATTERY_R.notificationId(),
            OverlayState.BLE_CONNECTION_LOST.notificationId()
          )
        )
      }
      UiState.SCANNING -> { null }
      UiState.DISCONNECTED -> { null }
      UiState.SHOW_WELCOME -> { null }
      UiState.MULTIPLE_TRANSMITTERS -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_multiple_transmitters)
      }
      UiState.TRANSMITTERS_EMPTY -> { null }
      UiState.TRANSMITTER_NOT_FOUND -> { null }
      UiState.SOMETHING_ELSE,
      UiState.OK_A -> {
        // we will clear all notifications, except MULTIPLE_TRANSMITTERS, FIRMWARE_AVAILABLE and APP_UPDATE_AVAILABLE
        NotificationPayload(notificationsToRemove = listOf(
            UiState.BLUETOOTH_NOT_ENABLED.notificationId(),
            UiState.INNER_BREACH_B.notificationId(),
            UiState.OUTER_BREACH_C.notificationId(),
            UiState.PAY_ATTENTION_F.notificationId(),
            UiState.CUTGUARD_BROKEN_I.notificationId(),
            UiState.INNER_BLIND_D.notificationId(),
            UiState.TOTAL_BLIND_E.notificationId(),
            UiState.WET_MOUNT_G.notificationId(),
            UiState.WET_MOUNT_X.notificationId(),
            UiState.WATER_INGRESS_DETECTION_J.notificationId(),
            UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K.notificationId(),
            UiState.INNER_BLIND_LEAK_L.notificationId(),
            UiState.NO_BATTERY_R.notificationId(),
            UiState.TRANSMITTER_DETACHED_V.notificationId(),
            UiState.TRANSMITTER_BATTERY_CHARGING_W.notificationId(),
            OverlayState.BATTERY_LOW.notificationId(),
            OverlayState.BLE_CONNECTION_LOST.notificationId()
          )
        )
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
      UiState.INNER_BLIND_D -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_inner_blind)
      }
      UiState.TOTAL_BLIND_E -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_total_blind)
      }
      UiState.PAY_ATTENTION_F -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_sweat)
      }
      UiState.WET_MOUNT_G -> {
        NotificationPayload(state.notificationId(), R.string.status_wet_mount_title)
      }
      UiState.WET_MOUNT_X -> {
        NotificationPayload(state.notificationId(), R.string.status_inner_blind_mount_title)
      }
      UiState.WATER_INGRESS_DETECTION_J -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_water)
      }
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_dirt)
      }
      UiState.INNER_BLIND_LEAK_L -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_inner_blind_leak_l)
      }
      UiState.NO_BATTERY_R -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_disconnection_battery)
      }
      UiState.TRANSMITTER_DETACHED_V -> {
        NotificationPayload(state.notificationId(), R.string.lock_no_info_v)
      }
      UiState.TRANSMITTER_BATTERY_CHARGING_W -> {
        NotificationPayload(notificationsToRemove = listOf(
            UiState.NO_BATTERY_R.notificationId(),
            UiState.TRANSMITTER_DETACHED_V.notificationId(),
            OverlayState.BATTERY_LOW.notificationId()
          )
        )
      }
    }

    payload?.let {
      notificationCenter.show(it, macAddress)
      lastNotification = Instant.now()
    }
  }

  /**
   * Send notification based on current OverlayState
   */
  fun send(macAddress: String, state: OverlayState) {
    // this will stop sending multiple notifications of same type immediately right after
    if(isSameIdAndSnoozing(macAddress, state.notificationId())) {
      return
    }

    latestState[macAddress] = state.notificationId()

    val payload = when(state) {
      OverlayState.NONE -> { null }
      OverlayState.BATTERY_LOW -> {
        // this is temporary and not *real* code - but here to present a way to do overlay state notifications
        NotificationPayload(state.notificationId(), R.string.lock_notification_battery_critical)
      }
      OverlayState.BLE_CONNECTION_LOST -> {
        NotificationPayload(state.notificationId(), R.string.lock_notification_ble_disconnected)
      }
    }

    payload?.let {
      notificationCenter.show(it, macAddress)
      lastNotification = Instant.now()
    }
  }

  private fun isSameIdAndSnoozing(macAddress: String, notificationId: Int): Boolean {
    if(latestState[macAddress] == notificationId) {
      lastNotification?.let {
        val snoozing = (Instant.now().toEpochMilli() - it.toEpochMilli()) <= NotificationScheduler.SNOOZE_TIME_MILLIS
        return snoozing
      }
    }
    return false
  }
  
  /**
   * cancel (remove) a specific notification
   */
  fun cancel(notificationId: Int) {
    notificationCenter.cancel(notificationId)
  }
}