package com.coloplast.olympus.ui.common.rx

object CommonRx {
  
  /*fun automaticLoginObservable(userService: UserService): Observable<CurrentUser> {
    return Me.get()
      .get()
      // only try to login if the current user is Empty
      .filter { it is CurrentUser.Empty }
      // try to auto login the user through the server
      .flatMap {
        return@flatMap userService.getUser()
          .subscribeOn(Schedulers.io())
          .map { currentUser: CurrentUser ->
            if (currentUser is CurrentUser.Empty) {
              val notLoggedIn = CurrentUser.LoggedOut()
              Me.get().set(notLoggedIn)
              return@map notLoggedIn
            }
            else {
              Me.get().set(currentUser)
              return@map currentUser
            }
          }
          .onErrorReturn {
            Timber.e("Error: $it")
            val notLoggedIn = CurrentUser.LoggedOut()
            Me.get().set(notLoggedIn)
            return@onErrorReturn notLoggedIn
          }
      }
  }*/
}