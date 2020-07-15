package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.models.CreateUserRequestDTO
import com.coloplast.olympus.datalayer.models.LoginRequestDTO
import com.coloplast.olympus.datalayer.models.SecurityCodeRequest
import com.coloplast.olympus.datalayer.models.UserResponseDTO
import com.coloplast.olympus.shared.models.CreateUserModel
import com.coloplast.olympus.shared.models.Credentials
import io.reactivex.Completable
import io.reactivex.Observable

class ApiServiceImpl : ApiService {
  
  private val api = Retrofit.serverApi

  override fun registration(createUserModel: CreateUserModel): Observable<UserResponseDTO> {
    val createUserDTO = CreateUserRequestDTO(
      email = createUserModel.email,
      password = createUserModel.password
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
}