package com.coloplast.olympus.ui.feature.transmitters.add

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterModel
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.transmitters.add.at1.AddTransmitterAT1Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at2.AddTransmitterAT2Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at3.AddTransmitterAT3Controller
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterFlow

class AddTransmitterFactoryImpl() : FlowFactory<AddTransmitterModel>() {
  
  private lateinit var model: AddTransmitterModel
  
  override fun initialize() {
    model = AddTransmitterModel()
  }
  
  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when (AddTransmitterFlow.fromIndex(currentIndex)) {
      AddTransmitterFlow.AddTransmitterAT1 -> AddTransmitterAT1Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT2 -> AddTransmitterAT2Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT3 -> AddTransmitterAT3Controller.routerTransaction()
      //AddTransmitterFlow.AddTransmitterAT9 -> AddTransmitterAT9Controller.routerTransaction(model.transmitterName)
      AddTransmitterFlow.Completed -> null
    }
  }
  
  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    if (result == null) {
      return
    }
    
    when (AddTransmitterFlow.fromIndex(currentIndex)) {
      AddTransmitterFlow.AddTransmitterAT1 -> { }
      AddTransmitterFlow.AddTransmitterAT2 -> { }
      AddTransmitterFlow.AddTransmitterAT3 -> { }
      //AddTransmitterFlow.AddTransmitterAT9 -> { model.macAddress = result.toString() }
      AddTransmitterFlow.Completed -> {  }
    }
  }
  
  override fun getData(): AddTransmitterModel {
    return model
  }
}