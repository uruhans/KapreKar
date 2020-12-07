package com.coloplast.olympus.datalayer.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class AzureAuthInterceptor(private val secretAzureCode: String) : Interceptor {

  private val queryAzureCode = "code"

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val updatedUrl = request.url.newBuilder().addQueryParameter(queryAzureCode, secretAzureCode).build()

    return chain.proceed(
      request.newBuilder()
        .url(updatedUrl)
        .build()
    )
  }
}