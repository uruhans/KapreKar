package com.coloplast.olympus.datalayer.api

import com.coloplast.olympus.datalayer.models.AppStateListDTO
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AppStateApi {

  @POST("appstate/compressed/user/{userId}")
  fun syncAppState(@Path("userId") userId: String, @Body appStateListDTO: List<AppStateListDTO>): Completable



}