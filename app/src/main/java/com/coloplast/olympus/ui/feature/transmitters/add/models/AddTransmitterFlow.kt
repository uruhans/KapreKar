package com.coloplast.olympus.ui.feature.transmitters.add.models

enum class AddTransmitterFlow(val index: Int) {
  AddTransmitterAT1(0),
  AddTransmitterAT2(1),
  AddTransmitterAT3(2),
  AddTransmitterAT4(3),
  AddTransmitterAT5(4),
  AddTransmitterAT6(5),
  AddTransmitterAT7(6),
  AddTransmitterBle1Disabled(7),
  AddTransmitterBle3Activated(8),
  AddTransmitterAT8(9),
  AddTransmitterAT9(10),
  AddTransmitterAT10(11),
  Completed(12);
  
  companion object {
    fun fromIndex(id: Int) : AddTransmitterFlow {
      return values().associateBy { flow: AddTransmitterFlow -> flow.index}[id] ?: Completed
    }
  }
}