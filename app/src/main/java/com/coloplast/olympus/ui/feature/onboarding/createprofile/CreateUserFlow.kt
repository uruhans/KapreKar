package com.coloplast.olympus.ui.feature.onboarding.createprofile


enum class CreateUserFlow {
  CreateUserSU1,
  StomaInfoSU2,
  EmailSU3,
  PasswordSU4,
  PhoneSU6,
  InsuranceSU7,
  ConfirmationSU8,
  Completed;

  companion object {
    fun fromIndex(index: Int) : CreateUserFlow {
      return values().associateBy { flow: CreateUserFlow -> flow.ordinal}[index] ?: Completed
    }
  }
}
