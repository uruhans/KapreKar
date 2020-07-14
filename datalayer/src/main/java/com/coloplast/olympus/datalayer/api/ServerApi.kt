package com.coloplast.olympus.datalayer.api

import com.coloplast.olympus.datalayer.models.CreateUserRequestDTO
import com.coloplast.olympus.datalayer.models.LoginRequestDTO
import com.coloplast.olympus.datalayer.models.UserResponseDTO
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ServerApi {

  @POST("/api/v1/Users/register")
  fun register(@Body createUserRequestDTO: CreateUserRequestDTO): Observable<UserResponseDTO>

  @POST("/api/v1/Auth/login")
  fun login(@Body loginRequestDTO: LoginRequestDTO): Observable<UserResponseDTO>

}