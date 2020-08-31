package com.coloplast.olympus.datalayer

import com.coloplast.olympus.datalayer.api.PackageApi
import com.coloplast.olympus.datalayer.api.ServerApi
import com.coloplast.olympus.datalayer.interceptors.AzureAuthInterceptor
import com.coloplast.olympus.datalayer.interceptors.ServerResponseInterceptor
import com.coloplast.olympus.datalayer.models.adapters.InstantAdapter
import com.coloplast.olympus.shared.AppSettings
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object AzureRetrofit {

  val packageApi: PackageApi = getRetrofit(AppSettings.AZURE_ENVIRONMENT_URL).create(PackageApi::class.java)

  private fun getRetrofit(withBaseUrl: String): Retrofit {
    return Retrofit.Builder()
      .client(getOkHttpClient())
      .baseUrl(withBaseUrl)
      .addConverterFactory(MoshiConverterFactory.create(getMoshi()))
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build()
  }

  private fun getOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(AzureAuthInterceptor(AppSettings.code))
      .addInterceptor(ServerResponseInterceptor())
      .addInterceptor(getHttpLoggingInterceptor())
      .connectTimeout(20, TimeUnit.SECONDS)
      .readTimeout(20, TimeUnit.SECONDS)
      .writeTimeout(20, TimeUnit.SECONDS)
      .build()
  }

  private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level =
      if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    return loggingInterceptor
  }

  private fun getMoshi(): Moshi {
    return Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }
}