package com.coloplast.olympus.shared.models

data class ServerException(val responseCode: Int, val responseMessage: String) : Exception()