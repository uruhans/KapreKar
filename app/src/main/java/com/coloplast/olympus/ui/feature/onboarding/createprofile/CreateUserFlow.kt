package com.coloplast.olympus.ui.feature.onboarding.createprofile

import com.coloplast.olympus.shared.types.TypeWithId


enum class CreateUserFlow(override val id: Int):
  TypeWithId {
  CreateUserSU1(0),
  StomaInfoSU2(1),
  EmailSU3(2),
  PasswordSU4(3),
  PhoneSU5(4),
  InsuranceSU7(5),
  ConfirmationSU8(6),
  Completed(7);

  companion object {
    fun fromIndex(index: Int) : CreateUserFlow {
      return values().associateBy { flow: CreateUserFlow -> flow.ordinal}[index] ?: Completed
    }
  }
}
