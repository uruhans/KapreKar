package com.coloplast.olympus.ui.feature.transmitters.add

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.transmitters.add.at1.AddTransmitterAT1Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at10.AddTransmitterAT10Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at2.AddTransmitterAT2Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at3.AddTransmitterAT3Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at4.AddTransmitterAT4Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at5.AddTransmitterAT5Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at6.AddTransmitterAT6Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at7.AddTransmitterAT7Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at8.AddTransmitterAT8Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at9.AddTransmitterAT9Controller
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterFlow
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterModel

class AddTransmitterFactoryImpl : FlowFactory<AddTransmitterModel>() {

  private lateinit var model: AddTransmitterModel

  override fun initialize() {
    model = AddTransmitterModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when (AddTransmitterFlow.fromIndex(currentIndex)) {
      AddTransmitterFlow.AddTransmitterAT1 -> AddTransmitterAT1Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT2 -> AddTransmitterAT2Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT3 -> AddTransmitterAT3Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT4 -> AddTransmitterAT4Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT5 -> AddTransmitterAT5Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT6 -> AddTransmitterAT6Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT7 -> AddTransmitterAT7Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT8 -> AddTransmitterAT8Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT9 -> AddTransmitterAT9Controller.routerTransaction(model.transmitterNameToSearchFor)
      AddTransmitterFlow.AddTransmitterAT10 -> AddTransmitterAT10Controller.routerTransaction()
      AddTransmitterFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    if (result == null) {
      return
    }

    if (AddTransmitterFlow.fromIndex(currentIndex) == AddTransmitterFlow.AddTransmitterAT4) {
      model.transmitterNameToSearchFor = result as String
    }
  }

  override fun getData(): AddTransmitterModel {
    return model
  }
}