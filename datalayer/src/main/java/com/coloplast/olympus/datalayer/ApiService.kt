package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.models.UserResponseDTO
import com.coloplast.olympus.shared.models.CreateUserModel
import com.coloplast.olympus.shared.models.Credentials
import io.reactivex.Completable
import io.reactivex.Observable

interface ApiService {

  fun registration(createUserModel: CreateUserModel): Observable<UserResponseDTO>
  fun login(credentials: Credentials): Observable<UserResponseDTO>

  fun requestSecurityCode(email: String): Completable

}