package com.coloplast.olympus.datalayer.api

import com.coloplast.olympus.datalayer.models.RawStreamDTO
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface PackageApi {

  @POST("raw/compressed/user/{userId}")
  fun syncRawPackages(@Path("userId") userId: String, @Body rawStreamDTO: List<RawStreamDTO>): Completable

}