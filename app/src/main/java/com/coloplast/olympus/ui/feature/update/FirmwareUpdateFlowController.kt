package com.coloplast.olympus.ui.feature.update

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.feature.update.model.FirmwareUpdateFlowModel
import org.koin.core.inject

class FirmwareUpdateFlowController : TopMenuFlowFactoryController<FirmwareUpdateFlowModel>() {

  private val factory: FirmwareUpdateFactoryImpl by inject()

  override fun getFlowFactory(): FlowFactory<FirmwareUpdateFlowModel> {
    return factory
  }

  companion object {
    private const val CONTROLLER_TAG = "controller_firmware_update_flow"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(FirmwareUpdateFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}