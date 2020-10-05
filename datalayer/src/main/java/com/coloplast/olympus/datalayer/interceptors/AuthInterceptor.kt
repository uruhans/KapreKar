package com.coloplast.olympus.datalayer.interceptors

import com.coloplast.olympus.shared.AppSettings
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val requestBuilder = chain.request().newBuilder()

    if(chain.request().url.queryParameter("sig") == null) {
      if (AppSettings.userToken.isNotBlank()) {
        requestBuilder.addHeader("Authorization", "Bearer ${AppSettings.userToken}")
      }
    }

    return chain.proceed(requestBuilder.build())
  }
}