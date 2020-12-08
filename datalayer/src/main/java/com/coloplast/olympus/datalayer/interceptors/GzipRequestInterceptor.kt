package com.coloplast.olympus.datalayer.interceptors

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import okio.BufferedSink
import okio.GzipSink
import okio.buffer

class GzipRequestInterceptor(private val secretAzureCode: String) : Interceptor {

  private val headerContentEncoding = "Content-Encoding"
  private val gZipContentEncoding = "gzip"
  private val queryCode = "code"

  override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()
    val updatedUrl = originalRequest.url.newBuilder()
      .addQueryParameter(queryCode, secretAzureCode)
      .build()

    val requestBuilder = originalRequest.newBuilder().url(updatedUrl)

    if (originalRequest.body == null || originalRequest.header(headerContentEncoding) != null) {
      return chain.proceed(requestBuilder.build())
    }

    val compressedRequest = requestBuilder
      .header(headerContentEncoding, gZipContentEncoding)
      .method(originalRequest.method, forceContentLength(gzip(originalRequest.body!!)))
      .build()

    return chain.proceed(compressedRequest)
  }

  /** https://github.com/square/okhttp/issues/350 */
  private fun forceContentLength(requestBody: RequestBody): RequestBody {
    val buffer = Buffer()
    requestBody.writeTo(buffer)

    return object : RequestBody() {
      override fun contentType(): MediaType? {
        return requestBody.contentType()
      }

      override fun contentLength(): Long {
        return buffer.size
      }

      override fun writeTo(sink: BufferedSink) {
        sink.write(buffer.snapshot())
      }
    }
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