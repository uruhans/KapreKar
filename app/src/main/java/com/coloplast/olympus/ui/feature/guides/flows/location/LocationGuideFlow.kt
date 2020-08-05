package com.coloplast.olympus.ui.feature.guides.flows.location

import com.coloplast.olympus.shared.types.TypeWithId


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 05/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class LocationGuideFlow(override val id: Int): TypeWithId {
  LocationGuideLG1(0),
  LocationGuideLG2(1),
  LocationGuideLG3(2),
  LocationGuideLG4(3),
  LocationGuideLG5(4),
  LocationGuideLG6(5),
  LocationGuideLG7(6),
  LocationGuideLG8(7),
  Completed(8);

  companion object {
    fun fromId(id: Int): LocationGuideFlow {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: Completed
    }
  }
}