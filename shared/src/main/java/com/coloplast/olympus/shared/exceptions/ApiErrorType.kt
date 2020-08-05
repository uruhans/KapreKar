package com.coloplast.olympus.shared.exceptions


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 17/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class ApiErrorType(val code: Int) {
  Unknown(10),

  //Auth
  AuthInvalidEmail(20), // Local validation handling should catch this,
  AuthInvalidPassword(21), // Local validation handling should catch this
  AuthEmailTaken(22),
  AuthPasswordTooWeak(23),
  AuthInvalidEmailOrPassword(24),
  AuthInvalidSecurityCode(25),
  ImageNotFound(31);

  companion object {
    fun fromErrorCode(errorCode: Int): ApiErrorType {
      return when (errorCode) {
        AuthInvalidEmail.code -> AuthInvalidEmail
        AuthInvalidPassword.code -> AuthInvalidPassword
        AuthEmailTaken.code -> AuthEmailTaken
        AuthPasswordTooWeak.code -> AuthPasswordTooWeak
        AuthInvalidEmailOrPassword.code -> AuthInvalidEmailOrPassword
        AuthInvalidSecurityCode.code -> AuthInvalidSecurityCode
        ImageNotFound.code -> ImageNotFound
        else -> Unknown
      }
    }
  }
}