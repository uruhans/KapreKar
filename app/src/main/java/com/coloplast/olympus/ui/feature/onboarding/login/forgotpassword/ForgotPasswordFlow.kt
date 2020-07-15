package com.coloplast.olympus.ui.feature.onboarding.login.forgotpassword

import com.coloplast.olympus.shared.types.TypeWithId


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 15/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class ForgotPasswordFlow(override val id: Int): TypeWithId {
  ForgotPasswordFP1(0),
  ForgotPasswordFP2(1),
  ForgotPasswordFP3(2),
  ForgotPasswordFP4(3),
  Completed(4);

  companion object {
    fun fromId(id: Int): ForgotPasswordFlow {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: Completed
    }
  }
}