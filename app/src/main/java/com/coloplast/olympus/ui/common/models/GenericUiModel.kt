package com.coloplast.olympus.ui.common.models

sealed class GenericUiModel : UiModel {
  
  open class Success<T>(val data: T) : GenericUiModel()
  
  data class Error(val state: State, val errorCode: Int) : GenericUiModel() {
    enum class State {
      ERROR_GENERIC, ERROR_NETWORK,
    }
  }
  
  data class PendingUser(val state: State = State.PENDING_USER_ACTION, val statusCode: Int = 0) :
    GenericUiModel() {
    enum class State {
      PENDING_USER_ACTION, ERROR_USER_INPUT
    }
  }
  
  data class Loading(val state: State = State.INDICATE_LOADING) : GenericUiModel() {
    enum class State {
      INDICATE_LOADING, PROCESSING
    }
  }
}