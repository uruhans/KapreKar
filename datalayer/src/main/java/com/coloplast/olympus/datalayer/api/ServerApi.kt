package com.coloplast.olympus.datalayer.api

import com.coloplast.olympus.datalayer.models.CreateUserRequestDTO
import com.coloplast.olympus.datalayer.models.CreateUserResponseDTO
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ServerApi {

  @POST("/api/v1/Users/register")
  fun createUser(@Body createUserRequestDTO: CreateUserRequestDTO): Observable<CreateUserResponseDTO>

}