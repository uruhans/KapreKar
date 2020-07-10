package com.coloplast.olympus.ui.feature.onboarding.createprofile

import com.coloplast.olympus.ui.common.TypeWithId


enum class CreateUserFlow(override val id: Int): TypeWithId {
  CreateUserSU1(0),
  StomaInfoSU2(1),
  EmailSU3(2),
  PasswordSU4(3),
  PhoneSU6(4),
  InsuranceSU7(5),
  ConfirmationSU8(6),
  Completed(7);

  companion object {
    fun fromId(id: Int): TypeWithId{
      return values().associateBy { enum: TypeWithId -> enum.id }[id] as? TypeWithId
        ?: throw NotImplementedError("Id $id was not found")
    }
  }
}
