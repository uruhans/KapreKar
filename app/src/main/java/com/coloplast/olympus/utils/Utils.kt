package com.coloplast.olympus.utils

object Utils {

  private const val UK_PREFIX_1 = "+44"
  private const val UK_PREFIX_2 = "0044"

  fun isUKNumber(phoneNumber: String?): Boolean {
    if(phoneNumber == null) {
      return false
    }

    return phoneNumber.contains(UK_PREFIX_1) || phoneNumber.contains(UK_PREFIX_2)
  }

}