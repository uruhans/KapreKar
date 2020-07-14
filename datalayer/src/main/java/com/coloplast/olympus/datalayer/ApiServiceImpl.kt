package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.models.CreateUserRequestDTO
import com.coloplast.olympus.datalayer.models.CreateUserResponseDTO
import com.coloplast.olympus.shared.models.CreateUserModel
import io.reactivex.Observable

class ApiServiceImpl : ApiService {
  
  private val api = Retrofit.serverApi

  override fun registration(createUserModel: CreateUserModel): Observable<CreateUserResponseDTO> {
    val createUserDTO = CreateUserRequestDTO(
      email = createUserModel.email,
      password = createUserModel.password
    )
    return api.register(createUserDTO)
  }
  
  
}