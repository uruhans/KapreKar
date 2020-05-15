package com.coloplast.olympus.ui.feature.transmitters.add.models

enum class AddTransmitterFlow {
  AddTransmitterAT1,
  AddTransmitterAT2,
  AddTransmitterAT3,
  AddTransmitterAT4,
  AddTransmitterAT5,
  //AddTransmitterAT9(2),
  Completed;
  
  companion object {
    fun fromIndex(index: Int) : AddTransmitterFlow {
      return values().associateBy { flow: AddTransmitterFlow -> flow.ordinal}[index] ?: Completed
    }
  }
}