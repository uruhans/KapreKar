package com.coloplast.olympus.datalayer.api

import com.coloplast.olympus.datalayer.models.TransmitterStreamDTO
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface PackageApi {

  @POST("{packageType}/compressed/user/{userId}")
  fun syncRawPackages(@Path("userId") userId: String, @Path("packageType") packageType: String, @Body rawStreamDTO: List<TransmitterStreamDTO>): Completable

}