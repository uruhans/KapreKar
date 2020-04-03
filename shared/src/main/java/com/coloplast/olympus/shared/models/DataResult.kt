package com.coloplast.olympus.shared.models

sealed class DataResult<T> {
  
  data class Success<T>(val data: T) : DataResult<T>()
  data class Empty<T>(val empty: Boolean = true) : DataResult<T>()
  
}