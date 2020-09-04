package com.coloplast.olympus.businesslayer.interfaces

import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.models.User
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

interface UserComponent {

  fun createUser(user: User): Observable<CurrentUser>
  fun login(credentials: Credentials): Observable<CurrentUser>
  fun automaticLogin(userId: UUID): Observable<CurrentUser>

  fun requestSecurityCode(email: String): Completable
  fun validateSecurityCode(securityCode: String, email: String): Completable
  fun changePassword(securityCode: String, email: String, password: String): Completable

  fun getLocalUser(userId: UUID): Observable<User>
}