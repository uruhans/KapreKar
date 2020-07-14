package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.models.CreateUserResponseDTO
import com.coloplast.olympus.shared.models.CreateUserModel
import io.reactivex.Observable

interface ApiService {

  fun registration(createUserModel: CreateUserModel): Observable<CreateUserResponseDTO>

}