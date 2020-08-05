package com.coloplast.olympus.shared.exceptions

import com.squareup.moshi.Json

data class CustomErrorModel(
  @Json(name = "Error") val error: Int,
  @Json(name = "ErrorName") val errorName: String?,
  @Json(name = "ErrorMessage") val errorMessage: String
) {
  var errorType: ApiErrorType = ApiErrorType.Unknown
}