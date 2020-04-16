package com.coloplast.olympus.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.R
import com.coloplast.olympus.activities.MainActivity
import com.coloplast.olympus.notifications.models.NotificationPayload

/**
 * This notification center will do the heavy lifting of actually creating, displaying and removing notifications.
 * It will also create NotificationChannel (if needed)
 */
class NotificationCenter(
  private val context: Context,
  private val notificationManager: NotificationManager
) {
  
  @StringRes
  private fun getChannelId(): Int {
    return R.string.lock_notification_channel_id
  }
  
  @StringRes
  private fun getChannelName(): Int {
    return R.string.lock_notification_channel_name
  }
  
  @StringRes
  private fun getChannelDescription(): Int {
    return R.string.lock_notification_channel_description
  }
  
  @DrawableRes
  private fun getNotificationIcon(): Int {
    return R.drawable.ic_detailed_plate_ok
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
    // make sure we have a notification channel for the notification
    createNotificationChannel()
    
    // first remove all notifications that are specified in the payload (if any)
    payload.notificationsToRemove.forEach { id ->
      cancel(id)
    }
    
    // create an intent for the snooze action
    val intent = Intent().apply {
      action = INTENT_FILTER_ACTION_SNOOZE
      setClass(context, SnoozeReceiver::class.java)
      putExtra(SCHEDULED_NOTIFICATION_ID, payload.id)
    }
    
    // put the intent into a pending intent that can be used in the action
    val snoozePendingIntent = PendingIntent.getBroadcast(
      context,
      payload.id,
      intent,
      PendingIntent.FLAG_ONE_SHOT
    )
    
    // create the notification
    val notification = NotificationCompat.Builder(context, channelId)
      .setSmallIcon(getNotificationIcon())
      .setWhen(System.currentTimeMillis())
      .setColor(ContextCompat.getColor(context, R.color.olympus_bluegreen))
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
    
    // and display it to the user
    with(NotificationManagerCompat.from(context)) {
      notify(payload.id, notification.build())
    }
  }
  
  @RequiresApi(26)
  fun deleteNotificationChannel() {
    notificationManager.deleteNotificationChannel(context.getString(getChannelId()))
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
      NotificationChannel(
        channelId, channelName, getImportance()
      ).apply {
        description = channelDescription
        notificationManager.createNotificationChannel(this)
      }
    }
  }
  
  private val channelId: String by lazy {
    context.getString(getChannelId())
  }
  
  private val channelName: String by lazy {
    context.getString(getChannelName())
  }
  
  private val channelDescription: String by lazy {
    context.getString(getChannelDescription())
  }
  
  companion object {
    // intent filters for the broadcast receiver, so we can reschedule a notification and later show it again
    const val INTENT_FILTER_ACTION_SNOOZE = "${BuildConfig.APPLICATION_ID}.NOTIFICATION_ACTION_SNOOZE"
    const val INTENT_FILTER_SHOW_NOTIFICATION = "${BuildConfig.APPLICATION_ID}.NOTIFICATION_SHOW"
    
    // the notification id that we wish to show
    const val SCHEDULED_NOTIFICATION_ID = "SCHEDULED_NOTIFICATION_ID"
  }
}