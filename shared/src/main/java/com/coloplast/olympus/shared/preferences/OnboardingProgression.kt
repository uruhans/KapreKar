package com.coloplast.olympus.shared.preferences

import com.coloplast.olympus.shared.types.TypeWithId

enum class OnboardingProgression(override val id: Int): TypeWithId {
  NOT_STARTED(-1),
  SIGN_UP(0),
  SETUP_GUIDE_START(1),
  SETUP_GUIDE_INTRODUCTION(2),
  SETUP_GUIDE_CONNECTING(3),
  SETUP_GUIDE_PREPARING(4),
  SETUP_GUIDE_APPLYING(5),
  COMPLETED(6);

  companion object {
    fun fromId(id: Int): OnboardingProgression {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: NOT_STARTED
    }
  }
}