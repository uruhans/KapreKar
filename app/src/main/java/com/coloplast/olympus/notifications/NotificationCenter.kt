package com.coloplast.olympus.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.R
import com.coloplast.olympus.activities.MainActivity
import com.coloplast.olympus.notifications.models.NotificationPayload
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.feature.settings.models.Sensors
import com.coloplast.olympus.ui.feature.statemachine.models.UiState

/**
 * This notification center will do the heavy lifting of actually creating, displaying and removing notifications.
 * It will also create NotificationChannel (if needed)
 */
class NotificationCenter(
  private val context: Context,
  private val notificationManager: NotificationManager,
  private val preferences: Preferences
) {

  private val channelId: String = context.getString(R.string.lock_notification_channel_id)
  private val channelName: String = context.getString(R.string.lock_notification_channel_name)
  private val channelDescription: String =
    context.getString(R.string.lock_notification_channel_description)

  private val ringingAlertChannelId: String =
    context.getString(R.string.lock_ringing_alarm_notification_channel_id)
  private val ringingAlertChannelName: String =
    context.getString(R.string.lock_ringing_alarm_notification_channel_name)
  private val ringingAlertChannelDescription: String =
    context.getString(R.string.lock_ringing_alarm_notification_channel_description)

  init {
    // make sure we have a notification channel for the notification
    createNotificationChannel()
    //show(NotificationPayload(UiState.OUTER_BREACH_C.notificationId(), R.string.lock_notification_outer))
  }

  /**
   * set the notification channel importance level
   *
   * Urgent (NotificationManager.IMPORTANCE_HIGH)
   *   Makes a sound and appears as a heads-up notification
   *
   * High (NotificationManager.IMPORTANCE_DEFAULT)
   *   Makes a sound
   *
   * Medium (NotificationManager.IMPORTANCE_LOW)
   *   No sound
   *
   * Low (NotificationManager.IMPORTANCE_MIN)
   *   No sound and does not appear in the status bar
   *
   * @return importance of notification (default: IMPORTANCE_DEFAULT)
   */
  @RequiresApi(24)
  private fun getImportance(): Int {
    return NotificationManager.IMPORTANCE_DEFAULT
  }

  private fun getPendingIntent(): PendingIntent {
    // create an explicit intent to open main activity
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
  }

  /**
   * show a notification based on the payload coming in
   */
  fun show(payload: NotificationPayload) {
    // first remove all notifications that are specified in the payload (if any)
    payload.notificationsToRemove.forEach { id ->
      cancel(id)
    }

    // create an intent for the snooze action
    val snoozeIntent = Intent().apply {
      action = INTENT_FILTER_ACTION_SNOOZE
      setClass(context, NotificationBroadcastReceiver::class.java)
      putExtra(SCHEDULED_NOTIFICATION_ID, payload.id)
    }

    // put the intent into a pending intent that can be used in the action
    val snoozePendingIntent = PendingIntent.getBroadcast(
      context,
      payload.id,
      snoozeIntent,
      PendingIntent.FLAG_ONE_SHOT
    )

    when (payload.id) {
      UiState.OUTER_BREACH_C.notificationId() -> {
        if (preferences.areRingingAlertsEnabled) {
          triggerRingingAlarmNotification(payload, snoozePendingIntent)
        }
        else {
          triggerRegularNotification(payload, snoozePendingIntent)
        }
      }
      UiState.INNER_BREACH_B.notificationId() -> {
        val ringingAlertSelectedSensors = Sensors.fromOrdinal(preferences.ringingAlertSensor)
        if (ringingAlertSelectedSensors == Sensors.ALL) {
          triggerRingingAlarmNotification(payload, snoozePendingIntent)
        }
        else {
          // only outer sensors enabled. Send regular notification.
          triggerRegularNotification(payload, snoozePendingIntent)
        }
      }
      else -> triggerRegularNotification(payload, snoozePendingIntent)
    }

  }

  private fun triggerRingingAlarmNotification(
    payload: NotificationPayload,
    snoozePendingIntent: PendingIntent
  ) {
    val dismissIntent = Intent().apply {
      action = INTENT_FILTER_ACTION_DISMISS
      setClass(context, NotificationBroadcastReceiver::class.java)
      putExtra(SCHEDULED_NOTIFICATION_ID, payload.id)
    }

    val dismissPendingIntent = PendingIntent.getBroadcast(
      context,
      payload.id,
      dismissIntent,
      PendingIntent.FLAG_ONE_SHOT
    )

    val notification = NotificationCompat.Builder(context, ringingAlertChannelId)
      .setSmallIcon(R.drawable.ic_detailed_plate_ok)
      .setContentText(context.getString(payload.body))
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .setAutoCancel(true)
      .addAction(
        R.drawable.ic_baseline_snooze_24,
        context.getString(R.string.lock_notification_action_snooze),
        snoozePendingIntent
      )
      .addAction(
        R.drawable.ic_baseline_cancel_24,
        context.getString(R.string.generic_dismiss),
        dismissPendingIntent
      )
      .build().apply {
        flags = Notification.FLAG_INSISTENT
      }

    // and display it to the user
    with(NotificationManagerCompat.from(context)) {
      notify(payload.id, notification)
    }
  }

  private fun triggerRegularNotification(
    payload: NotificationPayload,
    snoozePendingIntent: PendingIntent
  ) {
    val notification = NotificationCompat.Builder(context, channelId)
      .setSmallIcon(R.drawable.ic_detailed_plate_ok)
      .setWhen(System.currentTimeMillis())
      .setColor(ContextCompat.getColor(context, R.color.turquoise))
      .setContentText(context.getString(payload.body))
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      // Set the intent that will fire when the user taps the notification
      .setContentIntent(getPendingIntent())
      .setAutoCancel(true)
      .addAction(
        R.drawable.ic_baseline_snooze_24,
        context.getString(R.string.lock_notification_action_snooze),
        snoozePendingIntent
      )
      .build()

    with(NotificationManagerCompat.from(context)) {
      notify(payload.id, notification)
    }
  }

  @RequiresApi(26)
  fun deleteNotificationChannel() {
    notificationManager.deleteNotificationChannel(channelId)
  }

  /**
   * clear all notifications that are currently being displayed
   */
  fun clear() {
    notificationManager.cancelAll()
  }

  /**
   * remove a specific notification that is currently displayed (or ignore if notification doesn't exist - manually removed or never shown)
   */
  fun cancel(notificationId: Int) {
    notificationManager.cancel(notificationId)
  }

  private fun createNotificationChannel() {
    // only create the notification channel if we are above API 25 and the channel doesn't already exist
    if (Build.VERSION.SDK_INT >= 26 && notificationManager.getNotificationChannel(channelId) == null) {
      NotificationChannel(channelId, channelName, getImportance()).apply {
        description = channelDescription
        notificationManager.createNotificationChannel(this)
      }
    }
  }

  companion object {
    // intent filters for the broadcast receiver, so we can reschedule a notification and later show it again
    const val INTENT_FILTER_ACTION_SNOOZE =
      "${BuildConfig.APPLICATION_ID}.NOTIFICATION_ACTION_SNOOZE"
    const val INTENT_FILTER_ACTION_DISMISS =
      "${BuildConfig.APPLICATION_ID}.NOTIFICATION_ACTION_DISMISS"
    const val INTENT_FILTER_SHOW_NOTIFICATION = "${BuildConfig.APPLICATION_ID}.NOTIFICATION_SHOW"

    // the notification id that we wish to show
    const val SCHEDULED_NOTIFICATION_ID = "SCHEDULED_NOTIFICATION_ID"
  }
}