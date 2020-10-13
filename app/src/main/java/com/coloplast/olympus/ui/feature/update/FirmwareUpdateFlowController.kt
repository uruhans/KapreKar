package com.coloplast.olympus.ui.feature.update

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.feature.update.model.FirmwareUpdateFlow
import com.coloplast.olympus.ui.feature.update.model.FirmwareUpdateFlowModel
import org.koin.core.inject

class FirmwareUpdateFlowController : TopMenuFlowFactoryController<FirmwareUpdateFlowModel>() {

  private val factory: FirmwareUpdateFactoryImpl by inject()

  override fun getFlowFactory(): FlowFactory<FirmwareUpdateFlowModel> {
    return factory
  }

  override fun handleBack(): Boolean {

    if (factory.getCurrent() == FirmwareUpdateFlow.Updating.id) {
      return true
    } else if (factory.getCurrent() == FirmwareUpdateFlow.Completed.id) {
      closeFlow()
      return true
    }
    return super.handleBack()

  }

  companion object {
    private const val CONTROLLER_TAG = "controller_firmware_update_flow"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(FirmwareUpdateFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}