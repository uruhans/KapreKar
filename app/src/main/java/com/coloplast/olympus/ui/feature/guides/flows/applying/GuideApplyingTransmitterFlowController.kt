package com.coloplast.olympus.ui.feature.guides.flows.applying

import android.content.Intent
import android.net.Uri
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.shared.preferences.OnboardingProgression
import com.coloplast.olympus.shared.preferences.PreferencesImpl
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.dialogs.CallHelpDialogController
import com.coloplast.olympus.ui.feature.onboarding.setup.model.ApplyingFlowModel
import org.koin.core.inject

class GuideApplyingTransmitterFlowController : TopMenuFlowFactoryController<ApplyingFlowModel>(),
  MediaGuideController.Listener,
  CallHelpDialogController.Listener {

  private val factory: GuideApplyingTransmitterFactoryImpl by inject()
  private val preferences: PreferencesImpl by inject()
  private var callHelpDialogController: CallHelpDialogController? = null

  override fun getFlowFactory(): FlowFactory<ApplyingFlowModel> {
    return factory
  }

  override fun onStepCompleted(currentIndex: Int, data: ApplyingFlowModel): Boolean {
    when (GuideApplyingTransmitterFlow.fromIndex(currentIndex)) {

      GuideApplyingTransmitterFlow.AwaitingConnection -> {
        if (data.connectionSuccessful)
          factory.skipUntil(GuideApplyingTransmitterFlow.SetupCompleted.id)
        else
          factory.skipUntil(GuideApplyingTransmitterFlow.GuideAG15.id)

      }
      GuideApplyingTransmitterFlow.GuideAG15 -> {
        if (data.retryConnection) {
          factory.goto(GuideApplyingTransmitterFlow.GuideAG9.id)
        }
      }

      else -> return false
    }

    return false
  }

  override fun onFlowCompleted(data: ApplyingFlowModel) {
    preferences.setCurrentOnboardingProgression(OnboardingProgression.COMPLETED)
    preferences.setOnboardingCompletedTime()
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

  companion object {
    private const val CONTROLLER_TAG = "guide_applying_transmitters_flow"
    private const val DIALOG_CALL_HELP = 1005

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(GuideApplyingTransmitterFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}