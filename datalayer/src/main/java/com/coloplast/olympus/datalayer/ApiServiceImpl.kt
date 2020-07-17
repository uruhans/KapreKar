package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.models.*
import com.coloplast.olympus.shared.models.CreateUser
import com.coloplast.olympus.shared.models.Credentials
import io.reactivex.Completable
import io.reactivex.Observable

class ApiServiceImpl : ApiService {
  
  private val api = Retrofit.serverApi

  override fun registration(createUser: CreateUser): Observable<UserResponseDTO> {
    val createUserDTO = CreateUserRequestDTO(
      email = createUser.email,
      password = createUser.password
    )
    return api.register(createUserDTO)
  }

  override fun login(credentials: Credentials): Observable<UserResponseDTO> {
    val loginRequestDTO = LoginRequestDTO(
      credentials.email,
      credentials.password
    )
    return api.login(loginRequestDTO)
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