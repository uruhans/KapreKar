package com.coloplast.olympus.ui.feature.onboarding.dataconsent

import com.coloplast.olympus.shared.types.TypeWithId


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 10/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class DataConsentFlow(override val id: Int): TypeWithId {
  DataConsentDC1(0),
  DataConsentDC2(1),
  DataConsentDC3(2),
  DataConsentDC4(3),
  DataConsentDC5(4),
  ProfileComplete(5),
  Completed(6);

  companion object {
    fun fromIndex(id: Int): DataConsentFlow{
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: Completed
    }
  }
}