package com.coloplast.olympus.datalayer.models.adapters

import com.coloplast.olympus.datalayer.AzureRetrofit
import com.coloplast.olympus.datalayer.models.PowerPackageDTO
import com.coloplast.olympus.datalayer.models.RawPackageDTO
import com.coloplast.olympus.datalayer.models.StatusPackageDTO
import com.coloplast.olympus.datalayer.models.TransmitterValue
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class TransmitterValueAdapter {

  @ToJson
  fun toJson(transmitterValue: TransmitterValue) : Any? {
    return when(transmitterValue) {
      is RawPackageDTO -> {
        AzureRetrofit.getMoshi().adapter(RawPackageDTO::class.java).toJsonValue(transmitterValue)
      }
      is StatusPackageDTO -> {
        AzureRetrofit.getMoshi().adapter(StatusPackageDTO::class.java).toJsonValue(transmitterValue)
      }
      is PowerPackageDTO -> {
        AzureRetrofit.getMoshi().adapter(PowerPackageDTO::class.java).toJsonValue(transmitterValue)
      }
      else -> null
    }
  }

  @FromJson
  fun fromJson(value: String) : TransmitterValue? {
    return null
  }
}