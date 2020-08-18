package com.coloplast.olympus.businesslayer.components

import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.datalayer.ApiService
import com.coloplast.olympus.datalayer.ApiServiceImpl
import com.coloplast.olympus.datalayer.models.UserResponseDTO
import com.coloplast.olympus.shared.models.CreateUserModel
import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.preferences.Preferences
import io.reactivex.Completable
import io.reactivex.Observable


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 08/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class UserComponentImpl : UserComponent {

  private val apiService: ApiService = ApiServiceImpl()

  override fun createUser(createUserModel: CreateUserModel): Observable<CurrentUser> {
    return apiService.createUser(createUserModel)
      .map { setTokenAndReturnUser(it) }
  }

  override fun login(credentials: Credentials): Observable<CurrentUser> {
    return apiService.login(credentials)
      .map { setTokenAndReturnUser(it) }
  }

  private fun setTokenAndReturnUser(userResponseDTO: UserResponseDTO): CurrentUser {
    Preferences.get().userToken = userResponseDTO.token
    return CurrentUser.LoggedIn(userResponseDTO.user.toUser())
  }

  override fun requestSecurityCode(email: String) = apiService.requestSecurityCode(email)

  override fun validateSecurityCode(securityCode: String, email: String): Completable =
    apiService.validateSecurityCode(securityCode, email)

  override fun changePassword(securityCode: String, email: String, password: String): Completable =
    apiService.changePassword(securityCode, email, password)

}