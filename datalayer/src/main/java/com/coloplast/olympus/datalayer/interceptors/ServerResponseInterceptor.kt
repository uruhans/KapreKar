package com.coloplast.olympus.datalayer.interceptors

import com.coloplast.olympus.shared.models.ServerException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.Response

class ServerResponseInterceptor : Interceptor {
  private var jsonConverter = Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  
  override fun intercept(chain: Interceptor.Chain): Response {
    val response = chain.proceed(chain.request())
    
    if (!response.isSuccessful) {
      throw ServerException(response.code, response.message)
    }
    
    return response
  }
}
