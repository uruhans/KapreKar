package com.coloplast.olympus.ui.feature.guides.flows.component

import com.coloplast.olympus.shared.types.TypeWithId


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class ComponentGuideFlow(override val id: Int): TypeWithId {
  ComponentGuideCO1(0),
  ComponentGuideCO2(1),
  ComponentGuideCO3(2),
  ComponentGuideCO4(3),
  ComponentGuideCO5(4),
  ComponentGuideCO6(5),
  ComponentGuideCO7(6),
  ComponentGuideCO8(7),
  ComponentGuideCO9(8),
  ComponentGuideCO10(9),
  ComponentGuideCO11(10),
  ComponentGuideCO12(11),
  ComponentGuideCO13(12),
  ComponentGuideCO14(13),
  Completed(14);

  companion object {
    fun fromId(id: Int): ComponentGuideFlow {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: Completed
    }
  }
}