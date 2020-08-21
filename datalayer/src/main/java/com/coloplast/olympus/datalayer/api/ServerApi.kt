package com.coloplast.olympus.datalayer.api

import com.coloplast.olympus.datalayer.models.*
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.*

interface ServerApi {

  @POST("users/register")
  fun createUser(@Body userDTO: UserDTO): Observable<UserResponseDTO>

  @POST("auth/login")
  fun login(@Body credentialsDTO: CredentialsDTO): Observable<UserResponseDTO>

  @POST("securitycodes")
  fun requestSecurityCode(@Body securityCodeRequest: SecurityCodeRequest): Completable

  @GET("securitycodes")
  fun confirmCode(@Query("securityCode") code: String, @Query("email") email: String): Completable

  @PUT("auth/changepassword")
  fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): Completable
}