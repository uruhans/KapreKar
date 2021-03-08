package com.coloplast.olympus

import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.preferences.PreferencesImpl
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class Me(private val preferences: PreferencesImpl) {
  
  private var currentUser: CurrentUser = CurrentUser.Empty()
  private val userRelay: PublishRelay<CurrentUser> = PublishRelay.create()
  
  fun currentUserObservable(): Observable<CurrentUser> {
    return userRelay.publish()
      .refCount()
      .startWith(currentUser)
  }
  
  fun setCurrentUser(currentUser: CurrentUser, notifySubscribers: Boolean = true) {
    synchronized(this) {
      this.currentUser = currentUser
      
      if (notifySubscribers) {
        userRelay.accept(this.currentUser)
      }
    }
  }
  
  fun logout(notifySubscribers: Boolean = true) {
    preferences.clear()
    setCurrentUser(CurrentUser.LoggedOut(), notifySubscribers)
  }
}