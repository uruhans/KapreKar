package com.coloplast.olympus.ui.feature.onboarding.setup

import android.content.Intent
import android.net.Uri
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.dialogs.CallHelpDialogController
import com.coloplast.olympus.ui.feature.onboarding.setup.applying.SetupGuideApplyingSGA6Controller
import com.coloplast.olympus.ui.feature.onboarding.setup.model.ApplyingFlowModel
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideApplyingFlow
import org.koin.core.inject

class SetupGuideApplyingFlowController : TopMenuFlowFactoryController<ApplyingFlowModel>(), MediaGuideController.Listener,
  CallHelpDialogController.Listener {

  private val factory: SetupGuideApplyingFactoryImpl by inject()

  private var callHelpDialogController: CallHelpDialogController? = null

  override fun getFlowFactory(): FlowFactory<ApplyingFlowModel> {
    return factory
  }

  override fun onStepCompleted(currentIndex: Int, data: ApplyingFlowModel): Boolean {
    if(currentIndex == SetupGuideApplyingFlow.SetupGuideContinueSGATMH6.id) {
      if(data.SetupGuideContinueSGATMH6_Call_Support) {
        onQuestionClicked("CallSupport")
        return true
      }
    }
    else if(currentIndex == SetupGuideApplyingFlow.SetupGuideAttachedSuccessSGA13.id) {
      factory.skipNext()
    }
    else if(currentIndex == SetupGuideApplyingFlow.SetupGuideAttachedFailedSGA13_2.id) {
      if(data.SetupGuideAttachedFailedSGA13_2_Call_Support) {
        onQuestionClicked("CallSupport")
      }
      else {
        factory.goto(SetupGuideApplyingFlow.SetupGuideEnsureClosedSGA6.id)
      }
      return true
    }

    return false
  }

  override fun popUntil(currentIndex: Int) {
    if(currentIndex == SetupGuideApplyingFlow.SetupGuideEnsureClosedSGA6.id) {
      getFlowChildRouter().popToTag(SetupGuideApplyingSGA6Controller.TAG_CONTROLLER, HorizontalChangeHandler())
    }
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
    private const val CONTROLLER_TAG = "controller_setup_guide_applying_flow"
    private const val DIALOG_CALL_HELP = 1005

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SetupGuideApplyingFlowController())
          .tag(CONTROLLER_TAG)
    }
  }
}