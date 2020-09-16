package com.coloplast.olympus.datalayer.interceptors

import com.coloplast.olympus.shared.models.ServerException
import okhttp3.Interceptor
import okhttp3.Response

class ServerResponseInterceptor : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val response = chain.proceed(chain.request())
    
    if (!response.isSuccessful) {
      throw ServerException(response.code, response.message)
    }
    
    return response
  }
}