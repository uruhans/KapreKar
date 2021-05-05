package com.coloplast.olympus.businesslayer.interfaces

import com.coloplast.olympus.businesslayer.models.SyncResult
import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.models.User
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

interface UserComponent {

  fun clearLocalStorage(): Observable<SyncResult>

  fun createUser(user: User): Observable<CurrentUser>
  fun login(credentials: Credentials): Observable<CurrentUser>
  fun automaticLogin(userId: UUID, appVersion: String): Observable<CurrentUser>

  fun requestSecurityCode(email: String): Completable
  fun validateSecurityCode(securityCode: Int, email: String): Completable
  fun changePassword(securityCode: Int, email: String, password: String): Completable

  fun getSingleLocalUser(userId: UUID): Single<User>
  fun getLocalUser(userId: UUID): Observable<User>
  fun updateUserConsent(userId: UUID, consent: Boolean): Observable<User>
  fun updateUser(user: User): Observable<User>

  fun requestPhoneValidationSMS(phoneNumber: String): Completable
  fun validatePhoneCode(phoneNumber: String, code: Int): Completable

  fun syncUser(userId: UUID): Observable<SyncResult>
}