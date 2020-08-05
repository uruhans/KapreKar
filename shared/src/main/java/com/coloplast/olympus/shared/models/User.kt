package com.coloplast.olympus.shared.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
  val userId: String,
  val email: String
) : Parcelable