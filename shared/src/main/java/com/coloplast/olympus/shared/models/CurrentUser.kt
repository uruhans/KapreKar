package com.coloplast.olympus.shared.models

sealed class CurrentUser {
  
  /**
   * the class to represent a logged in user
   */
  data class LoggedIn(val user: User) : CurrentUser()
  
  /**
   * class to represent a user that is not logged in
   * We do this because RxJava does not support null
   */
  data class Empty(private val loggedIn: Boolean = false) : CurrentUser()
  data class LoggedOut(private val loggedIn: Boolean = false) : CurrentUser()
}