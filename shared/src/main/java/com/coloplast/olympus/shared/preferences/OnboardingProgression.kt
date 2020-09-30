package com.coloplast.olympus.shared.preferences

import com.coloplast.olympus.shared.types.TypeWithId

enum class OnboardingProgression(override val id: Int): TypeWithId {
  NOT_STARTED(-1),
  TERMS_AND_CONSENT(0),
  SIGN_UP(1),
  SETUP_GUIDE_START(2),
  SETUP_GUIDE_INTRODUCTION(3),
  SETUP_GUIDE_CONNECTING(4),
  SETUP_GUIDE_PREPARING(5),
  SETUP_GUIDE_APPLYING(6),
  COMPLETED(7);

  companion object {
    fun fromId(id: Int): OnboardingProgression {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: NOT_STARTED
    }
  }
}