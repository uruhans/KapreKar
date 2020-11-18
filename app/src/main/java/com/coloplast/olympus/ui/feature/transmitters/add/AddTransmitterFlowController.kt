package com.coloplast.olympus.ui.feature.transmitters.add

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.bluetooth.businesslayer.ServiceStateDispatcher
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.common.models.flow.EmptyModel
import com.coloplast.olympus.ui.dialogs.CallHelpDialogController
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuideConnectionFlowController
import com.coloplast.olympus.ui.feature.transmitters.TransmittersController
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterFlow
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterModel
import org.koin.core.inject

class AddTransmitterFlowController(args: Bundle) : TopMenuFlowFactoryController<AddTransmitterModel>(args),
  MediaGuideController.Listener,  CallHelpDialogController.Listener {

  private val startAtIndex = args.getInt(BUNDLE_START_FLOW_AT, 0)
  private val factory: AddTransmitterFactoryImpl by inject()
  private val serviceStateDispatcher: ServiceStateDispatcher by inject()

  private var callHelpDialogController: CallHelpDialogController? = null

  override fun startFlow(flowChildRouter: Router) {
    factory.overrideStartingPosition(startAtIndex)
    super.startFlow(flowChildRouter)
  }

  override fun getFlowFactory(): FlowFactory<AddTransmitterModel> {
    return factory
  }

  override fun onStepCompleted(currentIndex: Int, data: AddTransmitterModel): Boolean {
    if (AddTransmitterFlow.fromIndex(currentIndex) == AddTransmitterFlow.AddTransmitterAT7) {
      if(data.bluetoothEnabled) {
        factory.skipUntil(AddTransmitterFlow.AddTransmitterAT8.index)
      }
    }
    else if (AddTransmitterFlow.fromIndex(currentIndex) == AddTransmitterFlow.AddTransmitterAT8) {
      serviceStateDispatcher.serviceRelay.accept(ServiceStateDispatcher.ServiceState.STOP_SERVICE)
      serviceStateDispatcher.serviceRelay.accept(ServiceStateDispatcher.ServiceState.START_SERVICE)
      factory.incrementAndNext()
      return true
    }

    return false
  }

  override fun onQuestionClicked(screen: String) {
    callHelpDialogController = CallHelpDialogController.create(
      this,
      DIALOG_CALL_HELP,
      true
    )

    callHelpDialogController?.show()
  }

  override fun onCallClicked(requestCode: Int, number: String) {
    if(requestCode == DIALOG_CALL_HELP) {
      Intent(Intent.ACTION_DIAL).also {
        it.data = Uri.parse("tel:$number")
        startActivity(it)
      }
    }
  }

  override fun onDialogDismissed(requestCode: Int) {
    if(requestCode == DIALOG_CALL_HELP) {
      callHelpDialogController = null
    }
  }

  override fun onFlowCompleted(data: AddTransmitterModel) {

    if (args.getBoolean(FINISH_FLOW_TO_TRANSMITTER_CONTROLLER)) {

      with(getContentRouter()) {
        popToTag(HomeController.TAG_CONTROLLER)
        pushController(
        TransmittersController.routerTransaction()
          .pushChangeHandler(VerticalChangeHandler())
          .popChangeHandler(VerticalChangeHandler()))
      }

    }

    val closeFlow = targetController is SetupGuideConnectionFlowController
    super.onFlowCompleted(data)

    if(closeFlow) {
      (targetController as SetupGuideConnectionFlowController).onFlowCompleted(EmptyModel())
    }

  }

  companion object {
    private const val FINISH_FLOW_TO_TRANSMITTER_CONTROLLER = "finish_flow_to_transmitter_controller"
    private const val CONTROLLER_TAG = "add_transmitter_flow_controller"
    private const val BUNDLE_START_FLOW_AT = "BUNDLE_START_FLOW_AT"
    private const val DIALOG_CALL_HELP = 1005

    fun routerTransaction(startAtIndex: Int = 0, finishFlowToTransmitterController : Boolean = false): RouterTransaction {
      with(Bundle()) {
        putInt(BUNDLE_START_FLOW_AT, startAtIndex)
        putBoolean(FINISH_FLOW_TO_TRANSMITTER_CONTROLLER, finishFlowToTransmitterController)
        return RouterTransaction.with(AddTransmitterFlowController(this))
          .tag(CONTROLLER_TAG)
      }
    }
  }
}