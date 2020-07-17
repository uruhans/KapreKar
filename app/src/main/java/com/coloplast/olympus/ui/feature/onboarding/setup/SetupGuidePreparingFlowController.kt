package com.coloplast.olympus.ui.feature.onboarding.setup

import android.content.Intent
import android.net.Uri
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.EmptyModel
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.dialogs.CallHelpDialogController
import org.koin.core.inject

class SetupGuidePreparingFlowController : TopMenuFlowFactoryController<EmptyModel>(), MediaGuideController.Listener,
  CallHelpDialogController.Listener {

  private val factory: SetupGuidePreparingFactoryImpl by inject()

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

  companion object {
    private const val CONTROLLER_TAG = "controller_setup_guide_preparing_flow"
    private const val DIALOG_CALL_HELP = 1005

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SetupGuidePreparingFlowController())
          .tag(CONTROLLER_TAG)
    }
  }
}