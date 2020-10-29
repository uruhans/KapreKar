package com.coloplast.olympus.ui.feature.onboarding.setup

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.databinding.ControllerFlowBinding
import com.coloplast.olympus.shared.preferences.OnboardingProgression
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.PermissionHandler
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.common.models.flow.EmptyModel
import com.coloplast.olympus.ui.dialogs.CallHelpDialogController
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideConnectionFlow
import com.coloplast.olympus.ui.feature.transmitters.add.AddTransmitterFlowController
import com.coloplast.olympus.utils.OSUtils
import org.koin.core.inject
import timber.log.Timber

class SetupGuideConnectionFlowController : TopMenuFlowFactoryController<EmptyModel>(), PermissionHandler,
  MediaGuideController.Listener,
  CallHelpDialogController.Listener {

  private val factory: SetupGuideConnectionFactoryImpl by inject()
  private val preferences: Preferences by inject()
  private var callHelpDialogController: CallHelpDialogController? = null

  override fun getFlowFactory(): FlowFactory<EmptyModel> {
    return factory
  }

  override fun onViewBound(viewBinding: ControllerFlowBinding) {
    super.onViewBound(viewBinding)

    if(SetupGuideConnectionFlow.fromIndex(factory.getCurrent()) == SetupGuideConnectionFlow.SetupGuideFindTMCodeSGC5) {
      factory.previous()
    }
  }

  override fun onFlowCompleted(data: EmptyModel) {
    preferences.setCurrentOnboardingProgression(OnboardingProgression.SETUP_GUIDE_PREPARING)
    super.onFlowCompleted(data)
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
    if (requestCode == DIALOG_CALL_HELP) {
      Intent(Intent.ACTION_DIAL).also {
        it.data = Uri.parse("tel:$number")
        startActivity(it)
      }
    }
  }

  override fun onDialogDismissed(requestCode: Int) {
    if (requestCode == DIALOG_CALL_HELP) {
      callHelpDialogController = null
    }
  }

  override fun onStepCompleted(currentIndex: Int, data: EmptyModel): Boolean {
    // if bluetooth is disabled we should ask to enable it
    if (SetupGuideConnectionFlow.fromIndex(currentIndex) == SetupGuideConnectionFlow.SetupGuideBluetoothSGC1) {
      Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).also {
        startActivityForResult(it, REQUEST_CODE_BLUETOOTH_ENABLE)
      }
      return true
    }
    else if (SetupGuideConnectionFlow.fromIndex(currentIndex) == SetupGuideConnectionFlow.SetupGuideLocationSGC3) {
      requestPermissionAgain(
        this,
        REQUEST_CODE_PERMISSION_LOCATION,
        OSUtils.bleLocationPermission()
      )
      return true
    }
    else if (SetupGuideConnectionFlow.fromIndex(currentIndex) == SetupGuideConnectionFlow.SetupGuideFindTMCodeSGC5) {
      closeFlow()
      return true
    }

    return false
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      REQUEST_CODE_BLUETOOTH_ENABLE -> {
        if (resultCode == Activity.RESULT_CANCELED) {
          Timber.d("enable it, dude")
        }
        else {
          factory.incrementAndNext()
        }
      }
    }
  }

  override fun showPermissionRationale(requestCode: Int) {
    /* no-op */
    // we have no need to show this, as the current screen tells the user
  }

  override fun onPermissionGranted(requestCode: Int) {
    if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
      factory.incrementAndNext()
    }
  }

  override fun onPermissionDenied(requestCode: Int) {

  }

  override fun next(routerTransaction: RouterTransaction) {
    if (routerTransaction.controller() is AddTransmitterFlowController) {
      routerTransaction.controller().targetController = this

      getContentRouter().pushController(
        routerTransaction
          .pushChangeHandler(HorizontalChangeHandler())
          .popChangeHandler(HorizontalChangeHandler())
      )
    }
    else {
      super.next(routerTransaction)
    }
  }

  override fun handleBack(): Boolean {
    return if (getContentRouter().backstack.lastOrNull()?.controller() is AddTransmitterFlowController) {
      getFlowFactory().previous()
      getContentRouter().handleBack()
      true
    }
    else {
      super.handleBack()
    }
  }

  companion object {
    private const val CONTROLLER_TAG = "controller_setup_guide_connection_flow"
    private const val REQUEST_CODE_PERMISSION_LOCATION = 5001
    private const val REQUEST_CODE_BLUETOOTH_ENABLE = 5005
    private const val DIALOG_CALL_HELP = 1005

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SetupGuideConnectionFlowController())
        .tag(CONTROLLER_TAG)
    }
  }

}