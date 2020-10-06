package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.models.*
import com.coloplast.olympus.shared.models.AppVersion
import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.shared.models.FirmwareContent
import com.coloplast.olympus.shared.models.User
import io.reactivex.Completable
import io.reactivex.Observable

class ApiServiceImpl : ApiService {

  private val api = Retrofit.serverApi

  override fun createUser(user: User): Observable<UserResponseDTO> {
    return api.createUser(
      UserDTO(
        null,
        user.firstName,
        user.lastName,
        user.email,
        user.password,
        user.stomaType,
        user.surgeryDateInterval,
        user.phoneNumber,
        user.insuranceType,
        user.consent
      )
    )
  }

  override fun createAnonymousUser(): Observable<UserResponseDTO> {
    return api.createUser(UserDTO())
  }

  override fun updateUser(user: User): Completable {
    return api.updateUser(user.id.toString(), UserDTO.fromUser(user)).ignoreElements()
  }

  override fun login(credentials: Credentials): Observable<UserResponseDTO> {
    return api.login(
      CredentialsDTO(credentials.email, credentials.password)
    )
  }

  override fun requestSecurityCode(email: String): Completable {
    val securityCodeRequest = ChangePasswordRequest(
      email = email
    )
    return api.requestSecurityCode(securityCodeRequest)
  }

  override fun validateSecurityCode(securityCode: Int, email: String): Completable{
    val validateCodeRequest = ChangePasswordRequest(
      email = email,
      securityCode = securityCode
    )
    return api.confirmCode(validateCodeRequest)
  }

  override fun changePassword(securityCode: Int, email: String, password: String): Completable {
    val changePasswordRequest = ChangePasswordRequest(
      securityCode = securityCode,
      email = email,
      newPassword = password)
    return api.changePassword(changePasswordRequest)
  }

  override fun requestPhoneValidationSMS(phoneNumber: String): Completable {
    val phoneValidationRequestDTO = PhoneValidationRequestDTO(
        phoneNumber = phoneNumber
    )
    return api.requestPhoneValidationSMS(phoneValidationRequestDTO)
  }

  override fun validatePhoneCode(phoneNumber: String, code: Int): Completable {
    val phoneValidationRequestDTO = PhoneValidationRequestDTO(
      phoneNumber = phoneNumber,
      securityCode = code
    )
    return api.validatePhoneCode(phoneValidationRequestDTO)
  }

  override fun latestFirmware(): Observable<FirmwareContent> {
    return api.getLatestFirmwareVersion()
      .map { it.toFirmware() }
  }

  override fun downloadFirmware(url: String): Observable<ByteArray> {
    return api.downloadFirmware(url)
      .map { it.bytes() }
  }

  override fun latestAppVersion(): Observable<AppVersion> {
    return api.getLatestAppVersion()
      .map { it.toAppVersion() }
  }
}