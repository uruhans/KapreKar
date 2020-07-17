package com.coloplast.olympus.shared.models

import com.coloplast.olympus.shared.exceptions.CustomErrorModel

data class ServerException(val responseCode: Int, val responseMessage: String, val errorModel: CustomErrorModel? = null) : Exception()