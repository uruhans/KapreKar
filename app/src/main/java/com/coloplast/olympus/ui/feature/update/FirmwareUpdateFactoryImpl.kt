package com.coloplast.olympus.ui.feature.update

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.GenericConfirmationController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.feature.update.charging.FirmwareUpdateChargingController
import com.coloplast.olympus.ui.feature.update.initiate.FirmwareInitiateController
import com.coloplast.olympus.ui.feature.update.model.FirmwareUpdateFlow
import com.coloplast.olympus.ui.feature.update.model.FirmwareUpdateFlowModel
import com.coloplast.olympus.ui.feature.update.progress.FirmwareProgressController

class FirmwareUpdateFactoryImpl : FlowFactory<FirmwareUpdateFlowModel>() {

  private val firmwareUpdateFlowModel = FirmwareUpdateFlowModel()

  override fun initialize() {
    /* no-op */
  }

  override fun getRouterTransaction(currentIndex: Int): RouterTransaction? {
    return when (FirmwareUpdateFlow.fromIndex(currentIndex)) {
      FirmwareUpdateFlow.Initiate -> FirmwareInitiateController.routerTransaction()
      FirmwareUpdateFlow.TransmitterCharging -> FirmwareUpdateChargingController.routerTransaction()
      FirmwareUpdateFlow.Updating -> FirmwareProgressController.routerTransaction(firmwareUpdateFlowModel.macAddress!!)
      FirmwareUpdateFlow.UpdateFinished -> {
        GenericConfirmationController.routerTransaction(
          R.string.guide_update_transmitter_4_title,
          R.string.guide_update_transmitter_4_body,
          R.string.guide_update_transmitter_4_action,
          imageId = R.drawable.ic_checkmark,
          showCloseButton = false,
          showBackButton = false,
          imageTint = R.color.turquoise
        )
      }
      FirmwareUpdateFlow.Completed -> null
    }
  }

  override fun parseResultFromController(currentIndex: Int, result: Any?) {
    @Suppress("NON_EXHAUSTIVE_WHEN")
    when (FirmwareUpdateFlow.fromIndex(currentIndex)) {
      FirmwareUpdateFlow.TransmitterCharging -> firmwareUpdateFlowModel.macAddress = result as String
    }
  }

  override fun getData(): FirmwareUpdateFlowModel {
    return firmwareUpdateFlowModel
  }
}