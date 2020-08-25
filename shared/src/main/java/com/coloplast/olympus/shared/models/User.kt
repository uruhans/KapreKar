package com.coloplast.olympus.shared.models

import com.coloplast.olympus.shared.types.StomaType
import com.coloplast.olympus.shared.types.SyncState
import java.util.*

class User(
  var id: UUID? = null,
  var firstName: String? = null,
  var lastName: String? = null,
  var email: String? = null,
  var password: String? = null,
  var stomaType: StomaType? = null,
  var surgeryDateInterval: Int? = null,
  var phoneNumber: String? = null,
  var insuranceType: Int? = null,
  var consent: Boolean? = null,
  var syncState: SyncState = SyncState.Pending
)