package com.coloplast.olympus.datalayer.api

import com.coloplast.olympus.datalayer.models.*
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

interface ServerApi {

  @POST("/api/v1/Users/register")
  fun createUser(@Body createUserRequestDTO: CreateUserRequestDTO): Observable<UserResponseDTO>

  @POST("/api/v1/Auth/login")
  fun login(@Body loginRequestDTO: LoginRequestDTO): Observable<UserResponseDTO>

  @POST("/api/v1/SecurityCodes")
  fun requestSecurityCode(@Body securityCodeRequest: SecurityCodeRequest): Completable

  @GET("/api/v1/SecurityCodes")
  fun confirmCode(@Query("securityCode") code: String, @Query("email") email: String): Completable

  @PUT("/api/v1/Auth/changepassword")
  fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): Completable
}