package com.coloplast.olympus.ui.feature.update.model

import com.coloplast.olympus.shared.types.TypeWithId

enum class FirmwareUpdateFlow(override val id: Int) : TypeWithId {
  Initiate(0),
  TransmitterCharging(1),
  Updating(2),
  UpdateFinished(3),
  Completed(4);

  companion object {
    fun fromIndex(id: Int): FirmwareUpdateFlow {
      return values().associateBy { item: FirmwareUpdateFlow -> item.id }[id] ?: Completed
    }
  }
}