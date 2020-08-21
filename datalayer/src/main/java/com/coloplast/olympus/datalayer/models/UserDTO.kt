package com.coloplast.olympus.datalayer.models

import java.util.*

class CreateUserDTO(
  val id: UUID? = null,
  val firstName: String? = null,
  val lastName: String? = null,
  val email: String? = null,
  val password: String? = null,
  val stomaTypeId: Int? = null,
  val stomaSurgeryDateId: Int? = null,
  val phoneNumber: String? = null,
  val healthInsuranceId: Int? = null,
  val consent: Boolean = false
)
