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
  DataConsentDC6(6),
  CreateUserSU1(7),
  StomaInfoSU2(8),
  EmailSU3(9),
  PasswordSU4(10),
  PhoneSU5(11),
  PhoneValidationSU6(12),
  InsuranceSU7(13),
  ConfirmationSU8(14),
  Completed(15);

  companion object {
    fun fromIndex(id: Int): SignupFlow {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: Completed
    }
  }
}