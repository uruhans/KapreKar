package com.coloplast.olympus.notifications.models

import androidx.annotation.StringRes

/**
 * the payload describes what the notification will write to the user
 *
 * id: based on either UiState or OverlayState
 * body: the body of the notification
 * notificationsToRemove: a list of notification id's to remove
 */
class NotificationPayload(
  val id: Int,
  @StringRes val body: Int,
  val notificationsToRemove: List<Int> = emptyList(),
  val title: Int? = null
)