package com.coloplast.olympus.ui.feature.transmitters.add

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.ServiceStateDispatcher
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.PermissionHandler
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.common.models.flow.EmptyModel
import com.coloplast.olympus.ui.dialogs.CallHelpDialogController
import com.coloplast.olympus.ui.dialogs.ImageDialogController
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.onboarding.setup.SetupGuideConnectionFlowController
import com.coloplast.olympus.ui.feature.transmitters.TransmittersController
import com.coloplast.olympus.ui.feature.transmitters.add.at9.AddTransmitterAT9Controller
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterFlow
import com.coloplast.olympus.ui.feature.transmitters.add.models.AddTransmitterModel
import com.coloplast.olympus.utils.OSUtils
import org.koin.core.inject

class AddTransmitterFlowController(args: Bundle) : TopMenuFlowFactoryController<AddTransmitterModel>(args),
  MediaGuideController.Listener, CallHelpDialogController.Listener, PermissionHandler, BaseDialogController.Listener,
  ImageDialogController.Listener {

  private val startAtIndex = args.getInt(BUNDLE_START_FLOW_AT, 0)
  private val factory: AddTransmitterFactoryImpl by inject()
  private val bluetoothManager: BluetoothManager by inject()
  private val serviceStateDispatcher: ServiceStateDispatcher by inject()

  private var callHelpDialogController: CallHelpDialogController? = null

  private var imageDialogController: ImageDialogController? = null
  private var dialogController: DialogController? = null

  override fun startFlow(flowChildRouter: Router) {
    factory.overrideStartingPosition(startAtIndex)
    super.startFlow(flowChildRouter)

    requestPermission(this, REQUEST_CODE_PERMISSION_LOCATION, OSUtils.bleLocationPermission())
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

    if(closeFlow) {
      (targetController as SetupGuideConnectionFlowController).onFlowCompleted(EmptyModel())
    }

    super.onFlowCompleted(data)
  }

  override fun afterControllerPushed(topController: Controller) {
    super.afterControllerPushed(topController)

    if(topController is AddTransmitterAT9Controller) {
      serviceStateDispatcher.startService()
    }
  }

  override fun afterControllerPopped(topController: Controller?) {
    super.afterControllerPopped(topController)

    // if we move back from the searching controller - we want to make sure we aren't actually still connected to it
    // this could happen if something weird occurred, where the transmitter has been deleted from the local database, but
    // the service would actually still be connected to it
    if(topController is MediaGuideController && topController.args.getString(MediaGuideController.SCREEN_NAME) == AddTransmitterFlow.AddTransmitterAT8.name) {
      bluetoothManager.disconnectTransmitter(factory.getData().transmitterNameToSearchFor)
    }
  }

  override fun showPermissionRationale(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_PERMISSION_LOCATION -> {
        imageDialogController = ImageDialogController.create(
          this,
          REQUEST_CODE_PERMISSION_RATIONALE,
          R.string.dialog_permission_rationale_location_title,
          R.string.dialog_permission_rationale_location_message,
          R.string.dialog_permission_rationale_location_ask_again,
          topImage = R.drawable.ic_bluetooth,
          verticalButtonLayout = true,
          dismissAllowed = true
        )

        imageDialogController?.show()
      }
    }
  }

  override fun onPermissionGranted(requestCode: Int) {
    /* no-op */
  }

  override fun onPermissionDenied(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_PERMISSION_LOCATION -> {
        showPermissionRationale(requestCode)
      }
    }
  }

  override fun onPositiveButtonClicked(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_PERMISSION_LOCATION -> {
        dialogController?.dismissDialog(withCallback = false)
        dialogController = null
      }
      REQUEST_CODE_PERMISSION_RATIONALE -> {
        imageDialogController?.dismissDialog(withCallback = true)
      }
    }
  }

  override fun onDialogDismissed(requestCode: Int) {
    when(requestCode) {
      DIALOG_CALL_HELP -> {
        callHelpDialogController = null
      }
      REQUEST_CODE_PERMISSION_RATIONALE -> {
        requestPermissionAgain(this, REQUEST_CODE_PERMISSION_LOCATION, OSUtils.bleLocationPermission())
      }
    }
  }

  override fun onCloseClicked(requestCode: Int) {
    /* no-op */
  }

  companion object {
    private const val FINISH_FLOW_TO_TRANSMITTER_CONTROLLER = "finish_flow_to_transmitter_controller"
    private const val CONTROLLER_TAG = "add_transmitter_flow_controller"
    private const val BUNDLE_START_FLOW_AT = "BUNDLE_START_FLOW_AT"
    private const val DIALOG_CALL_HELP = 1005

    private const val REQUEST_CODE_PERMISSION_LOCATION = 5001
    private const val REQUEST_CODE_PERMISSION_RATIONALE = 5002

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