package com.coloplast.olympus.businesslayer.components

import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.datalayer.ApiService
import com.coloplast.olympus.datalayer.ApiServiceImpl
import com.coloplast.olympus.shared.models.CreateUser
import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.preferences.Preferences
import io.reactivex.Observable


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 08/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class UserComponentImpl: UserComponent {

  private val apiService: ApiService = ApiServiceImpl()

  override fun registration(createUser: CreateUser): Observable<CurrentUser> {
    return apiService.registration(createUser)
      .map {
        Preferences.get().userToken = it.token
        CurrentUser.LoggedIn(
          it.user.toUser()
        )
      }
  }

  override fun login(credentials: Credentials): Observable<CurrentUser> {
    return apiService.login(credentials)
      .map {
        Preferences.get().userToken = it.token
        CurrentUser.LoggedIn(
          it.user.toUser()
        )
      }
  }

  override fun requestSecurityCode(email: String) = apiService.requestSecurityCode(email)

}