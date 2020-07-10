package com.coloplast.olympus.ui.feature.onboarding.dataconsent

import com.coloplast.olympus.ui.common.TypeWithId


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 10/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class DataConsentFlow(override val id: Int): TypeWithId {
  DataConsentDC1(1),
  DataConsentDC2(2),
  DataConsentDC3(3),
  DataConsentDC4(4),
  DataConsentDC5(5),
  Completed(6);

  companion object {
    fun fromId(id: Int): DataConsentFlow{
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: Completed
    }
  }
}