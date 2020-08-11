package com.coloplast.olympus.ui.feature.onboarding.setup

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.FlowFactoryController
import com.coloplast.olympus.ui.common.models.flow.EmptyModel
import com.coloplast.olympus.ui.dialogs.CallHelpDialogController
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideIntroductionFlow
import org.koin.core.inject
import timber.log.Timber

class SetupGuideIntroFlowController : FlowFactoryController<EmptyModel>(), MediaGuideController.Listener,
  CallHelpDialogController.Listener {

  private val factory: SetupGuideIntroFactoryImpl by inject()
  private var callHelpDialogController: CallHelpDialogController? = null

  override fun getFlowFactory(): FlowFactory<EmptyModel> {
    return factory
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

  override fun onStepCompleted(currentIndex: Int, data: EmptyModel): Boolean {
    // if bluetooth is disabled we should ask to enable it
    if(SetupGuideIntroductionFlow.fromIndex(currentIndex) == SetupGuideIntroductionFlow.SetupGuideBluetoothSGI2) {
      Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).also {
        startActivityForResult(it, REQUEST_CODE_BLUETOOTH_ENABLE)
      }
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

  companion object {
    private const val CONTROLLER_TAG = "controller_setup_guide_intro_flow"
    private const val REQUEST_CODE_BLUETOOTH_ENABLE = 5005
    private const val DIALOG_CALL_HELP = 1005

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SetupGuideIntroFlowController())
          .tag(CONTROLLER_TAG)
    }
  }
}