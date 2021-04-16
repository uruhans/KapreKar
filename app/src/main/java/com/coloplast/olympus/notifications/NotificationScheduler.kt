package com.coloplast.olympus.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * A class to re-schedule notifications if the user snoozes a notification
 *
 * snooze time is 15 minutes
 * postpone time is 5 minutes
 */
class NotificationScheduler {

  fun snoozeNotification(context: Context, macAddress: String, notificationId: Int, isPostponed: Boolean = false) {
    val pendingIntent = createPendingAlarmIntent(context, macAddress, notificationId)
    val triggerAtMillis = System.currentTimeMillis() + if (isPostponed) POSTPONED_TIME_MILLIS else SNOOZE_TIME_MILLIS

    // schedule the next alarm for this notification
    if (Build.VERSION.SDK_INT >= 23) {
      with(context.getSystemService(Context.ALARM_SERVICE) as AlarmManager) {
        setExactAndAllowWhileIdle(
          AlarmManager.RTC_WAKEUP,
          triggerAtMillis,
          pendingIntent
        )
      }
    }
    else {
      with(context.getSystemService(Context.ALARM_SERVICE) as AlarmManager) {
        setExact(
          AlarmManager.RTC_WAKEUP,
          triggerAtMillis,
          pendingIntent
        )
      }
    }
  }
  
  private fun createPendingAlarmIntent(context: Context, macAddress: String, notificationId: Int): PendingIntent {
    val intent = Intent().apply {
      action = NotificationCenter.INTENT_FILTER_SHOW_NOTIFICATION
      setClass(context, NotificationBroadcastReceiver::class.java)
      putExtra(NotificationCenter.SCHEDULED_NOTIFICATION_ID, notificationId)
      putExtra(NotificationCenter.MACADDRESS, macAddress)
    }
    return PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_ONE_SHOT)
  }

  companion object {
    val SNOOZE_TIME_MILLIS = 15L * 60 * 1000 // 15 minutes
    val POSTPONED_TIME_MILLIS = 5L * 60 * 1000 // 5 minutes
  }
}