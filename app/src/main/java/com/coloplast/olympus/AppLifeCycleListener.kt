package com.coloplast.olympus

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.coloplast.olympus.shared.AppSettings
import timber.log.Timber

class AppLifeCycleListener: LifecycleObserver {

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  fun onResume() {
    AppSettings.appInBackground = false
    Timber.d("App in Foreground - onResume")
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  fun onPause() {
    AppSettings.appInBackground = true
    Timber.d("App in Backgroudd - onPause")
  }

}