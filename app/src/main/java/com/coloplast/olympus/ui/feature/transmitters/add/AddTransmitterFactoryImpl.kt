package com.coloplast.olympus.ui.feature.transmitters.add

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.transmitters.add.at2.AddTransmitterAT2Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at3.AddTransmitterAT3Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at4.AddTransmitterAT4Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at5.AddTransmitterAT5Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at6.AddTransmitterAT6Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at7.AddTransmitterAT7Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at8.AddTransmitterAT8Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at9.AddTransmitterAT9Controller
import com.coloplast.olympus.ui.feature.transmitters.add.ble1.AddTransmitterBle1Controller
import com.coloplast.olympus.ui.feature.transmitters.add.ble3.AddTransmitterBle3Controller
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterFlow
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterModel

class AddTransmitterFactoryImpl : FlowFactory<AddTransmitterModel>() {

  private lateinit var model: AddTransmitterModel

  override fun initialize() {
    model = AddTransmitterModel()
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when (AddTransmitterFlow.fromIndex(currentIndex)) {
      AddTransmitterFlow.AddTransmitterAT1 -> InfoPageController.routerTransaction(
        iconId = R.drawable.ic_icon_guides,
        titleId = R.string.guide_at1_title,
        descriptionId = R.string.guide_at1_body,
        positiveButtonTextId = R.string.generic_continue,
        showCloseButton = true,
        showBackButton = true
      )
      AddTransmitterFlow.AddTransmitterAT2 -> AddTransmitterAT2Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT3 -> AddTransmitterAT3Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT4 -> AddTransmitterAT4Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT5 -> AddTransmitterAT5Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT6 -> AddTransmitterAT6Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT7 -> AddTransmitterAT7Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterBle1Disabled -> AddTransmitterBle1Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterBle3Activated -> AddTransmitterBle3Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT8 -> AddTransmitterAT8Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT9 -> AddTransmitterAT9Controller.routerTransaction(model.transmitterNameToSearchFor)
      AddTransmitterFlow.AddTransmitterAT10 -> GenericConfirmationController.routerTransaction(
        imageId = R.drawable.ic_checkmark,
        titleId = R.string.guide_at10_title,
        descriptionId = R.string.guide_at10_body,
        buttonId = R.string.generic_continue
      )
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
    else if (AddTransmitterFlow.fromIndex(currentIndex) == AddTransmitterFlow.AddTransmitterAT7) {
      model.bluetoothEnabled = result as Boolean
    }
  }

  override fun getData(): AddTransmitterModel {
    return model
  }
}