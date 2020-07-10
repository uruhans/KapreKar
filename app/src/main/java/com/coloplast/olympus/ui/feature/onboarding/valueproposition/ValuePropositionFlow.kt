package com.coloplast.olympus.ui.feature.onboarding.valueproposition

import com.coloplast.olympus.shared.types.TypeWithId


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 29/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class ValuePropositionFlow(override val id: Int):
  TypeWithId {
  ValuePropositionVP1(0),
  ValuePropositionVP2(1),
  ValuePropositionVP3(2),
  Completed(3);

  companion object {
    fun fromId(id: Int): TypeWithId {
      return values().associateBy { enum: TypeWithId -> enum.id }[id] as? TypeWithId
        ?: throw NotImplementedError("Id $id was not found")
    }
  }
}