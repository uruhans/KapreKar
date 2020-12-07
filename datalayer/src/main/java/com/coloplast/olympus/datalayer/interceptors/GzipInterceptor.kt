package com.coloplast.olympus.datalayer.interceptors

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.BufferedSink
import okio.GzipSink
import okio.buffer

class GzipInterceptor : Interceptor {

  private val headerContentEncoding = "Content-Encoding"
  private val gZipContentEncoding = "gzip"

  override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()

    if (originalRequest.body == null || originalRequest.header(headerContentEncoding) != null) {
      return chain.proceed(originalRequest)
    }

    val compressedRequest = originalRequest.newBuilder()
      .header(headerContentEncoding, gZipContentEncoding)
      .method(originalRequest.method, gzip(originalRequest.body!!))
      .build()

    return chain.proceed(compressedRequest)
  }

  private fun gzip(body: RequestBody): RequestBody {
    return object : RequestBody() {
      override fun contentType(): MediaType? {
        return body.contentType()
      }

      override fun contentLength(): Long {
        return -1 // We don't know the compressed length in advance!
      }

      override fun writeTo(sink: BufferedSink) {
        val gzipSink = GzipSink(sink).buffer()
        body.writeTo(gzipSink)
        gzipSink.close()
      }
    }
  }

}