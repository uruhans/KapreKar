package com.coloplast.olympus.ui.feature.guides.flows.tour

import com.coloplast.olympus.shared.types.TypeWithId


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class AppTourFlow(override val id: Int): TypeWithId {
  AppTourAP1(0),
  AppTourAP2(1),
  AppTourAP3(2),
  AppTourAP4(3),
  AppTourAP5(4),
  AppTourAP6(5),
  AppTourAP7(6),
  AppTourAP8(7),
  AppTourAP9(8),
  AppTourAP10(9),
  AppTourAP11(10),
  AppTourAP12(11),
  AppTourAP13(12),
  Completed(13);

  companion object {
    fun fromId(id: Int): AppTourFlow {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: Completed
    }
  }
}