package com.coloplast.olympus.shared.types


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 11/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class Sensors(override val id: Int): TypeWithId{
  ALL(0), OUTER(1);

  companion object {
    fun fromId(id: Int): Sensors {
      return values().associateBy { enum: TypeWithId -> enum.id }[id]
        ?: throw NotImplementedError("Id $id was not found")
    }
  }
}