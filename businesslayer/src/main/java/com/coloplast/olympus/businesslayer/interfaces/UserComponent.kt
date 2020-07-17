package com.coloplast.olympus.businesslayer.interfaces

import com.coloplast.olympus.shared.models.CreateUser
import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.shared.models.CurrentUser
import io.reactivex.Completable
import io.reactivex.Observable


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 08/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
interface UserComponent {

  fun registration(createUser: CreateUser): Observable<CurrentUser>
  fun login(credentials: Credentials): Observable<CurrentUser>

  fun requestSecurityCode(email: String): Completable
  fun validateSecurityCode(securityCode: String, email: String): Completable
}