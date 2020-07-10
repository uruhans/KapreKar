package com.coloplast.olympus.ui.feature.transmitters.add.models

enum class AddTransmitterFlow(val index: Int) {
  AddTransmitterAT1(0),
  AddTransmitterAT2(1),
  AddTransmitterAT3(2),
  AddTransmitterAT4(3),
  AddTransmitterAT5(4),
  AddTransmitterAT6(5),
  AddTransmitterAT7(6),
  AddTransmitterAT8(7),
  AddTransmitterAT9(8),
  AddTransmitterAT10(9),
  Completed(10);
  
  companion object {
    fun fromIndex(id: Int) : AddTransmitterFlow {
      return values().associateBy { flow: AddTransmitterFlow -> flow.index}[id] ?: Completed
    }
  }
}