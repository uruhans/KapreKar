package com.coloplast.olympus.datalayer.models

import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.types.SyncState
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class UserDTO(
  val userId: String? = null,
  val firstName: String? = null,
  val lastName: String? = null,
  val email: String? = null,
  val password: String? = null,
  val countryCode: String? = null,
  val stomaTypeId: Int? = null,
  val stomaSurgeryDateId: Int? = null,
  val phoneNumber: String? = null,
  val healthInsuranceId: Int? = null,
  val sensorPatchSize: Int? = null,
  val consent: Boolean = false,
  val appVersion: String? = null,
  val platform: String? = "Android"
) {

  /**
   * Converts a UserDTO into a User
   *
   * NOTICE: SyncState is defaulted to 'Uploaded' because the expectation is that
   * the server already have the latest version of the user already.
   */
  fun toUser(): User {
    return User(
      UUID.fromString(userId),
      firstName,
      lastName,
      email,
      password,
      countryCode,
      stomaTypeId,
      stomaSurgeryDateId,
      phoneNumber,
      healthInsuranceId,
      sensorPatchSize,
      consent,
      appVersion ?: "",
      platform ?: "",
      SyncState.Uploaded
    )
  }

  companion object {
    fun fromUser(user: User) : UserDTO {
      return UserDTO(
        user.id.toString(),
        user.firstName,
        user.lastName,
        user.email,
        user.password,
        user.country,
        user.stomaType,
        user.surgeryDateInterval,
        user.phoneNumber,
        user.insuranceType,
        user.sensorPatchSize,
        user.consent,
        user.appVersion,
        user.platform
      )
    }
  }
}
