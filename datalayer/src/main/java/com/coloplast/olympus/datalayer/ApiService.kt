package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.models.UserResponseDTO
import com.coloplast.olympus.shared.models.AppVersion
import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.shared.models.FirmwareContent
import com.coloplast.olympus.shared.models.User
import io.reactivex.Completable
import io.reactivex.Observable

interface ApiService {

  fun createUser(user: User): Observable<UserResponseDTO>
  fun createAnonymousUser() : Observable<UserResponseDTO>

  fun updateUser(user: User) : Completable

  fun login(credentials: Credentials): Observable<UserResponseDTO>

  fun requestSecurityCode(email: String): Completable
  fun validateSecurityCode(securityCode: Int, email: String): Completable
  fun changePassword(securityCode: Int, email: String, password: String): Completable

  fun requestPhoneValidationSMS(phoneNumber: String): Completable
  fun validatePhoneCode(phoneNumber: String, code: Int): Completable

  fun latestFirmware() : Observable<FirmwareContent>
  fun downloadFirmware(url: String) : Observable<ByteArray>

  fun latestAppVersion(): Observable<AppVersion>
}