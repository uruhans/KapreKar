package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.models.ChangePasswordRequest
import com.coloplast.olympus.datalayer.models.CredentialsDTO
import com.coloplast.olympus.datalayer.models.UserDTO
import com.coloplast.olympus.datalayer.models.UserResponseDTO
import com.coloplast.olympus.shared.models.Credentials
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
        user.consent ?: false
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

}