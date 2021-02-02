package com.coloplast.olympus

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.coloplast.olympus.shared.AppSettings

class AppLifeCycleListener: LifecycleObserver {

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  fun onResume() {
    AppSettings.appInBackground = false
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  fun onPause() {
    AppSettings.appInBackground = true
  }

}