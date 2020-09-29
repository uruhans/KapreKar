package com.coloplast.olympus.ui.feature.onboarding.signup

import com.coloplast.olympus.shared.types.TypeWithId


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 29/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class SignupFlow(override val id: Int): TypeWithId {
  TermsOfUseTU(0),
  DataConsentDC1(1),
  DataConsentDC2(2),
  DataConsentDC3(3),
  DataConsentDC4(4),
  DataConsentDC5(5),
  CreateUserSU1(6),
  StomaInfoSU2(7),
  EmailSU3(8),
  PasswordSU4(9),
  PhoneSU5(10),
  PhoneValidationSU6(11),
  InsuranceSU7(12),
  ConfirmationSU8(13),
  Completed(14);

  companion object {
    fun fromIndex(id: Int): SignupFlow {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: Completed
    }
  }
}