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
 */
class NotificationScheduler {
  
  @Suppress("PrivatePropertyName")
  private val SNOOZE_TIME_MILLIS = 15L * 60 * 1000 // 15 minutes
  
  fun snoozeNotification(context: Context, notificationId: Int) {
    val pendingIntent = createPendingAlarmIntent(context, notificationId)
    val triggerAtMillis = System.currentTimeMillis() + SNOOZE_TIME_MILLIS
    
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
  
  private fun createPendingAlarmIntent(context: Context, notificationId: Int): PendingIntent {
    val intent = Intent().apply {
      action = NotificationCenter.INTENT_FILTER_SHOW_NOTIFICATION
      setClass(context, SnoozeReceiver::class.java)
      putExtra(NotificationCenter.SCHEDULED_NOTIFICATION_ID, notificationId)
    }
    
    return PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_ONE_SHOT)
  }
}