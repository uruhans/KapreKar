package com.coloplast.olympus.datalayer.api

import com.coloplast.olympus.datalayer.models.ChangePasswordRequest
import com.coloplast.olympus.datalayer.models.CredentialsDTO
import com.coloplast.olympus.datalayer.models.UserDTO
import com.coloplast.olympus.datalayer.models.UserResponseDTO
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ServerApi {

  @POST("users/register")
  fun createUser(@Body userDTO: UserDTO): Observable<UserResponseDTO>

  @PUT("users/{userId}")
  fun updateUser(@Path("userId") userId: String, @Body userDTO: UserDTO) : Observable<UserResponseDTO>

  @POST("auth/login")
  fun login(@Body credentialsDTO: CredentialsDTO): Observable<UserResponseDTO>

  @POST("securitycodes")
  fun requestSecurityCode(@Body changePasswordRequest: ChangePasswordRequest): Completable

  @POST("securitycodes/validate")
  //fun confirmCode(@Query("securityCode") code: String, @Query("email") email: String): Completable
  fun confirmCode(@Body changePasswordRequest: ChangePasswordRequest): Completable

  @PUT("auth/changepassword")
  fun changePassword(@Body changePasswordRequest: ChangePasswordRequest): Completable
}