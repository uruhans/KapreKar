package com.coloplast.olympus.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.R
import com.coloplast.olympus.activities.MainActivity
import com.coloplast.olympus.notifications.models.NotificationPayload
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.shared.preferences.PreferencesImpl
import com.coloplast.olympus.shared.types.Sensors
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import java.time.LocalTime

/**
 * This notification center will do the heavy lifting of actually creating, displaying and removing notifications.
 * It will also create NotificationChannel (if needed)
 */
class NotificationCenter(
  private val context: Context,
  private val notificationManager: NotificationManager,
  private val preferences: PreferencesImpl
) {

  private val statusNotificationsChannelId: String =
    context.getString(R.string.lock_notification_channel_id)
  private val statusNotificationsChannelName: String =
    context.getString(R.string.lock_notification_channel_name)
  private val statusNotificationsChannelDescription: String =
    context.getString(R.string.lock_notification_channel_description)

  private val ringingAlertChannelId =
    context.getString(R.string.lock_ringing_alarm_notification_channel_id)
  private val ringingAlertChannelName =
    context.getString(R.string.lock_ringing_alarm_notification_channel_name)
  private val ringingAlertChannelDescription =
    context.getString(R.string.lock_ringing_alarm_notification_channel_description)

  init {
    // make sure we have a notification channel for the notification
    createNotificationChannels()
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
  fun show(payload: NotificationPayload, macAddress: String) {
    // first remove all notifications that are specified in the payload (if any)
    payload.notificationsToRemove.forEach { id ->
      cancel(id)
    }

    if (payload.id == Int.MIN_VALUE) {
      return
    }

    if (notificationManager.activeNotifications.any { it.id == payload.id }) {
      return
    }

    // create an intent for the snooze action
    val snoozeIntent = Intent().apply {
      action = INTENT_FILTER_ACTION_SNOOZE
      setClass(context, NotificationBroadcastReceiver::class.java)
      putExtra(SCHEDULED_NOTIFICATION_ID, payload.id)
      putExtra(MACADDRESS, macAddress)
    }

    // put the intent into a pending intent that can be used in the action
    val snoozePendingIntent = PendingIntent.getBroadcast(
      context,
      payload.id,
      snoozeIntent,
      PendingIntent.FLAG_ONE_SHOT
    )

    when (payload.id) {
      OverlayState.BATTERY_LOW.notificationId(),
      OverlayState.BLE_CONNECTION_LOST.notificationId(),
      UiState.NO_BATTERY_R.notificationId(),
      UiState.CUTGUARD_BROKEN_I.notificationId(),
      UiState.WATER_INGRESS_DETECTION_J.notificationId(),
      UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K.notificationId(),
      UiState.TOTAL_BLIND_E.notificationId(),
      UiState.PAY_ATTENTION_F.notificationId(),
      UiState.OUTER_BREACH_C.notificationId() -> {
        if (isItNightTime() || preferences.areRingingAlertsEnabled) {
          triggerRingingAlarmNotification(payload, snoozePendingIntent, macAddress)
        }
        else {
          triggerRegularNotification(payload, snoozePendingIntent, macAddress)
        }
      }
      UiState.INNER_BREACH_B.notificationId() -> {
        if ((isItNightTime() && preferences.bedtimeAlertSensor == Sensors.ALL)
          || (preferences.areRingingAlertsEnabled && preferences.ringingAlertSensor == Sensors.ALL)
        ) {
          triggerRingingAlarmNotification(payload, snoozePendingIntent, macAddress)
        }
        else {
          // only outer sensors enabled. Send regular notification.
          triggerRegularNotification(payload, snoozePendingIntent, macAddress)
        }
      }
      else -> triggerRegularNotification(payload, snoozePendingIntent, macAddress)
    }
  }

  private fun isItNightTime(): Boolean {
    val now = LocalTime.now()
    return (now.isAfter(preferences.bedtimeStart) || now.isBefore(preferences.bedtimeEnd))
  }

  private fun triggerRingingAlarmNotification(
    payload: NotificationPayload,
    snoozePendingIntent: PendingIntent,
    macAddress: String
  ) {
    val dismissIntent = Intent().apply {
      action = INTENT_FILTER_ACTION_DISMISS
      setClass(context, NotificationBroadcastReceiver::class.java)
      putExtra(SCHEDULED_NOTIFICATION_ID, payload.id)
      putExtra(MACADDRESS, macAddress)
    }

    val dismissPendingIntent = PendingIntent.getBroadcast(
      context,
      payload.id,
      dismissIntent,
      PendingIntent.FLAG_ONE_SHOT
    )

    val notificationBuilder = NotificationCompat.Builder(context, ringingAlertChannelId)
      .setSmallIcon(R.drawable.ic_detailed_plate_ok)
      .setContentText(context.getString(payload.body))
      .setCategory(Notification.CATEGORY_ALARM)
      .setDefaults(Notification.DEFAULT_ALL)
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setAutoCancel(false)
      // NO SOUND when we are in the app
      .setSilent(AppSettings.appInBackground.not())
      .addAction(
        R.drawable.ic_baseline_snooze_24,
        context.getString(R.string.lock_notification_action_snooze),
        snoozePendingIntent
      )
      .setFullScreenIntent(dismissPendingIntent, true)

    payload.title?.let {
      notificationBuilder.setContentTitle(context.getString(it))
    }

    if (payload.postPonedMilliseconds > 0L) {
      broadCastPostponedNotification(payload, macAddress)
    }
    else {
      // and display it to the user
      with(NotificationManagerCompat.from(context)) {
        notify(payload.id, notificationBuilder.build())
      }
    }
  }

  private fun triggerRegularNotification(
    payload: NotificationPayload,
    snoozePendingIntent: PendingIntent,
    macAddress: String
  ) {

    val notificationBuilder = NotificationCompat.Builder(context, statusNotificationsChannelId)
      .setSmallIcon(R.drawable.ic_detailed_plate_ok)
      .setWhen(System.currentTimeMillis())
      .setColor(ContextCompat.getColor(context, R.color.turquoise))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      // Set the intent that will fire when the user taps the notification
      .setContentIntent(getPendingIntent())
      // NO SOUND when we are in the app
      .setSilent(AppSettings.appInBackground.not())
      .setAutoCancel(true)
      .addAction(
        R.drawable.ic_baseline_snooze_24,
        context.getString(R.string.lock_notification_action_snooze),
        snoozePendingIntent
      )

    payload.title?.let {
      notificationBuilder.setContentTitle(context.getString(it))
    }

    notificationBuilder.setContentText(context.getString(payload.body))

    if (payload.postPonedMilliseconds > 0L) {
      broadCastPostponedNotification(payload, macAddress)
    }
    else {
      with(NotificationManagerCompat.from(context)) {
        notify(payload.id, notificationBuilder.build())
      }
    }
  }

  private fun broadCastPostponedNotification(payload: NotificationPayload, macAddress: String) {
    val postponedIntent = Intent().apply {
      action = INTENT_FILTER_ACTION_POSTPONED
      setClass(context, NotificationBroadcastReceiver::class.java)
      putExtra(SCHEDULED_NOTIFICATION_ID, payload.id)
      putExtra(MACADDRESS, macAddress)
      putExtra(POSTPONED_MILLISECONDS, payload.postPonedMilliseconds)

    }
    context.sendBroadcast(postponedIntent)
  }

  @RequiresApi(26)
  fun deleteNotificationChannel(channelId: String) {
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

  private fun createNotificationChannels() {
    // only create the notification channel if we are above API 25 and the channel doesn't already exist
    if (Build.VERSION.SDK_INT >= 26) {
      // Status notifications
      if (notificationManager.getNotificationChannel(statusNotificationsChannelId) == null) {
        NotificationChannel(
          statusNotificationsChannelId,
          statusNotificationsChannelName,
          getImportance()
        ).apply {
          description = statusNotificationsChannelDescription
          notificationManager.createNotificationChannel(this)
        }
      }

      // Ringing alert notification channel.
      if (notificationManager.getNotificationChannel(ringingAlertChannelId) == null) {
        val ringingAlertNotificationChannel = NotificationChannel(
          ringingAlertChannelId, ringingAlertChannelName, NotificationManager.IMPORTANCE_HIGH
        ).apply {
          val ringtoneUri = Settings.System.DEFAULT_RINGTONE_URI
          val audioAttributes =
            AudioAttributes.Builder()
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .setUsage(AudioAttributes.USAGE_ALARM)
              .build()
          description = ringingAlertChannelDescription
          setSound(ringtoneUri, audioAttributes)
        }
        notificationManager.createNotificationChannel(ringingAlertNotificationChannel)
      }
    }
  }

  companion object {
    // intent filters for the broadcast receiver, so we can reschedule a notification and later show it again
    const val INTENT_FILTER_ACTION_POSTPONED =
      "${BuildConfig.APPLICATION_ID}.NOTIFICATION_ACTION_POSTPONED"
    const val INTENT_FILTER_ACTION_SNOOZE =
      "${BuildConfig.APPLICATION_ID}.NOTIFICATION_ACTION_SNOOZE"
    const val INTENT_FILTER_ACTION_DISMISS =
      "${BuildConfig.APPLICATION_ID}.NOTIFICATION_ACTION_DISMISS"
    const val INTENT_FILTER_SHOW_NOTIFICATION = "${BuildConfig.APPLICATION_ID}.NOTIFICATION_SHOW"

    // the notification id that we wish to show
    const val SCHEDULED_NOTIFICATION_ID = "SCHEDULED_NOTIFICATION_ID"
    const val MACADDRESS = "MACADDRESS"
    const val POSTPONED_MILLISECONDS = "POSTPONED_MILLISECONDS"
  }
}