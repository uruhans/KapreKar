package com.coloplast.olympus.ui.feature.onboarding.setup

import android.content.Intent
import android.net.Uri
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.shared.preferences.OnboardingProgression
import com.coloplast.olympus.shared.preferences.PreferencesImpl
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.dialogs.CallHelpDialogController
import com.coloplast.olympus.ui.feature.onboarding.setup.model.ApplyingFlowModel
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideApplyingFlow
import com.coloplast.olympus.ui.feature.onboarding.setup.model.TransmitterCheckerSelectionType
import org.koin.core.inject

class SetupGuideApplyingFlowController : TopMenuFlowFactoryController<ApplyingFlowModel>(),
  MediaGuideController.Listener,
  CallHelpDialogController.Listener {

  private val factory: SetupGuideApplyingFactoryImpl by inject()
  private val preferences: PreferencesImpl by inject()
  private var callHelpDialogController: CallHelpDialogController? = null

  override fun getFlowFactory(): FlowFactory<ApplyingFlowModel> {
    return factory
  }

  override fun onStepCompleted(currentIndex: Int, data: ApplyingFlowModel): Boolean {
    when(SetupGuideApplyingFlow.fromIndex(currentIndex)) {
      SetupGuideApplyingFlow.SetupGuideTransmitterCheckerSGA8 -> {
        when (data.transmitterCheckerSelectionType ) {
          TransmitterCheckerSelectionType.NOT_SET -> return true
          TransmitterCheckerSelectionType.FIXED -> {
            factory.skipUntil(SetupGuideApplyingFlow.SetupGuideReadyAttachSGA9.id)
          }
          TransmitterCheckerSelectionType.LOOSE -> {
            factory.skipUntil(SetupGuideApplyingFlow.SetupGuideWaitSGATMH1.id)
          }
          TransmitterCheckerSelectionType.NOT_SURE -> {
            factory.skipUntil(SetupGuideApplyingFlow.SetupGuideNoProblemSGATMH2.id)
          }
        }
      }
      SetupGuideApplyingFlow.SetupGuideContinueSGATMH6 -> {
        if (data.SetupGuideContinueSGATMH6_Call_Support) {
          onQuestionClicked("CallSupport")
          return true
        }
      }
      SetupGuideApplyingFlow.SetupGuideAttachedSuccessSGA13 -> {
        factory.skipNext()
      }
      SetupGuideApplyingFlow.SetupGuideWaitSGATMH1 -> {
        factory.skipUntil(SetupGuideApplyingFlow.SetupGuideEnsureClosedSGATMH3.id)
      }
      SetupGuideApplyingFlow.AwaitingConnection -> {
        if (!data.connectionSuccessful) {
          factory.skipUntil(SetupGuideApplyingFlow.SetupGuideAttachedFailedSGA13_2.id)
        }
      }
      SetupGuideApplyingFlow.SetupGuideAttachedFailedSGA13_2 -> {
        if (data.SetupGuideAttachedFailedSGA13_2_Call_Support) {
          onQuestionClicked("CallSupport")
        }
        else {
          factory.goto(SetupGuideApplyingFlow.SetupGuideEnsureClosedSGA6.id)
        }
        return true
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

  override fun popUntil(currentIndex: Int) {
    if (currentIndex == SetupGuideApplyingFlow.SetupGuideEnsureClosedSGA6.id) {
      getFlowChildRouter().popToTag(
        SetupGuideApplyingFlow.SetupGuideEnsureClosedSGA6.name,
        HorizontalChangeHandler()
      )
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
    private const val CONTROLLER_TAG = "controller_setup_guide_applying_flow"
    private const val DIALOG_CALL_HELP = 1005

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SetupGuideApplyingFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}