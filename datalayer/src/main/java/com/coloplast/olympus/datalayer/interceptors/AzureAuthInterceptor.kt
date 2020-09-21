package com.coloplast.olympus.datalayer.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class AzureAuthInterceptor(private val code: String)  : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val url = chain.request().url
    val requestBuilder = chain.request().newBuilder()

    if(url.query.isNullOrBlank()) {
      requestBuilder.url("$url?code=$code")
    }
    else {
      requestBuilder.url("$url&code=$code")
    }

    return chain.proceed(requestBuilder.build())
  }
}