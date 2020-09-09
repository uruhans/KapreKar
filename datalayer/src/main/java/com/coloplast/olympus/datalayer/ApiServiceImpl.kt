package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.models.*
import com.coloplast.olympus.shared.models.Credentials
import com.coloplast.olympus.shared.models.TransmitterPackage
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.types.PackageType
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
        user.stomaType?.id,
        user.surgeryDateInterval,
        user.phoneNumber,
        user.insuranceType,
        user.consent ?: false
      )
    )
  }

  override fun createAnonymousUser(): Observable<UserResponseDTO> {
    return api.createUser(UserDTO())
  }

  override fun updateUser(user: User): Observable<UserResponseDTO> {
    return api.updateUser(user.id.toString(), UserDTO.fromUser(user))
  }

  override fun login(credentials: Credentials): Observable<UserResponseDTO> {
    return api.login(
      CredentialsDTO(credentials.email, credentials.password)
    )
  }

  override fun requestSecurityCode(email: String): Completable {
    val securityCodeRequest = SecurityCodeRequest(email)
    return api.requestSecurityCode(securityCodeRequest)
  }

  override fun validateSecurityCode(securityCode: String, email: String): Completable =
    api.confirmCode(securityCode, email)

  override fun changePassword(securityCode: String, email: String, password: String): Completable {
    val changePasswordRequest = ChangePasswordRequest(securityCode, email, password)
    return api.changePassword(changePasswordRequest)
  }

}