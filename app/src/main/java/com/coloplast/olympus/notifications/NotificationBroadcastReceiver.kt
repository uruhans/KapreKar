package com.coloplast.olympus.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * A broadcast receiver that will receive several types of intents
 *
 * * SNOOZE intent
 *    - will schedule an alarm manager to notify the user of a notification at a later time
 * * DISMISS intent
 *    - remove current notification
 * * SHOW intent
 *    - once the alarm manager hits expected time, it will send an intent hit here, which will then show the notification again
 */
class NotificationBroadcastReceiver : BroadcastReceiver(), KoinComponent {
  
  private val notificationScheduler: NotificationScheduler by inject()
  private val notificationManager: OlympusNotificationManager by inject()
  
  override fun onReceive(context: Context?, intent: Intent?) {
    val action = intent?.action ?: ""
    intent?.extras?.getInt(NotificationCenter.SCHEDULED_NOTIFICATION_ID)?.let { notificationId ->
      when(action){
        NotificationCenter.INTENT_FILTER_ACTION_POSTPONED -> {
          val macAddress = intent.extras?.getString(NotificationCenter.MACADDRESS)!!
          val postponedMilliseconds = intent.extras?.getLong(NotificationCenter.POSTPONED_MILLISECONDS)!!
          // postpone the notification
          notificationScheduler.snoozeNotification(context!!, macAddress, notificationId, postponedMilliseconds)
        }
        NotificationCenter.INTENT_FILTER_ACTION_SNOOZE -> {
          val macAddress = intent.extras?.getString(NotificationCenter.MACADDRESS)!!

          // snooze the notification
          notificationScheduler.snoozeNotification(context!!, macAddress, notificationId)

          // and cancel the current notification
          notificationManager.cancel(notificationId)
        }
        NotificationCenter.INTENT_FILTER_ACTION_DISMISS -> notificationManager.cancel(notificationId)
        NotificationCenter.INTENT_FILTER_SHOW_NOTIFICATION -> {
          val macAddress = intent.extras?.getString(NotificationCenter.MACADDRESS)!!

          // show the notification
          notificationManager.send(macAddress, notificationId, isScheduledNotification = true)
        }
      }
    }
  }
}