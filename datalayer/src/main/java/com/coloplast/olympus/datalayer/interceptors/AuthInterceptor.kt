package com.coloplast.olympus.datalayer.interceptors

import com.coloplast.olympus.shared.AppSettings
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {

  private val querySignature = "sig"
  private val headerAuthorization = "Authorization"
  private val tokenBearer = "Bearer"

  override fun intercept(chain: Interceptor.Chain): Response {
    val requestBuilder = chain.request().newBuilder()

    if(chain.request().url.queryParameter(querySignature) == null) {
      if (AppSettings.userToken.isNotBlank()) {
        requestBuilder.addHeader(headerAuthorization, "$tokenBearer ${AppSettings.userToken}")
      }
    }

    return chain.proceed(requestBuilder.build())
  }
}