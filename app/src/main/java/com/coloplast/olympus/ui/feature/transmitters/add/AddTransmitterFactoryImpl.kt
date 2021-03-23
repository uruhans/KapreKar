package com.coloplast.olympus.ui.feature.transmitters.add

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.InfoPageController
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.transmitters.add.at2.AddTransmitterAT2Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at4.AddTransmitterAT4Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at7.AddTransmitterAT7Controller
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
    val transactionTag = AddTransmitterFlow.fromIndex(currentIndex).name
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
      AddTransmitterFlow.AddTransmitterAT3 -> MediaGuideController.routerTransaction(
        resourceId = R.drawable.image_at3,
        titleId = R.string.guide_at3_title,
        descriptionId = R.string.guide_at3_body,
        position = 1,
        totalCount = 5,
        screenName = transactionTag,
        showCloseButton = false
      )
      AddTransmitterFlow.AddTransmitterAT4 -> AddTransmitterAT4Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT5 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.at5_insert_charger_cable,
        titleId = R.string.guide_at5_title,
        descriptionId = R.string.guide_at5_body,
        position = 2,
        totalCount = 5,
        screenName = transactionTag,
        showCloseButton = false
      )
      AddTransmitterFlow.AddTransmitterAT6 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.at6_connect_to_power,
        titleId = R.string.guide_at6_title,
        descriptionId = R.string.guide_at6_body,
        position = 3,
        totalCount = 5,
        screenName = transactionTag,
        showCloseButton = false
      )
      AddTransmitterFlow.AddTransmitterAT7 -> AddTransmitterAT7Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterBle1Disabled -> AddTransmitterBle1Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterBle3Activated -> AddTransmitterBle3Controller.routerTransaction()
      AddTransmitterFlow.AddTransmitterAT8 -> MediaGuideController.routerTransaction(
        resourceId = R.raw.at8_press_button_pairing_mode,
        titleId = R.string.guide_at8_title,
        descriptionId = R.string.guide_at8_body,
        position = 5,
        totalCount = 5,
        screenName = transactionTag,
        showCloseButton = false,
        showContinueButton = true
      )
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