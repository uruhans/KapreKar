package com.coloplast.olympus.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * A broadcast receiver that will receive two types of intents
 *
 * * SNOOZE intent
 *    - will schedule an alarm manager to notify the user of a notification at a later time
 * * SHOW intent
 *    - once the alarm manager hits expected time, it will send an intent hit here, which will then show the notification again
 */
class SnoozeReceiver : BroadcastReceiver(), KoinComponent {
  
  private val notificationScheduler: NotificationScheduler by inject()
  private val notificationManager: OlympusNotificationManager by inject()
  
  override fun onReceive(context: Context?, intent: Intent?) {
    val action = intent?.action ?: ""
    val notificationId = intent?.extras?.getInt(NotificationCenter.SCHEDULED_NOTIFICATION_ID)
    
    notificationId?.let {
      if (action == NotificationCenter.INTENT_FILTER_ACTION_SNOOZE) {
        // snooze the notification
        notificationScheduler.snoozeNotification(context!!, notificationId)
        
        // and cancel the current notification
        notificationManager.cancel(notificationId)
      }
      else if (action == NotificationCenter.INTENT_FILTER_SHOW_NOTIFICATION) {
        // show the notification
        notificationManager.send(notificationId)
      }
    }
  }
}