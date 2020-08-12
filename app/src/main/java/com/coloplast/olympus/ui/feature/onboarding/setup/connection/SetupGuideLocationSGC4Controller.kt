package com.coloplast.olympus.ui.feature.onboarding.setup.connection

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.databinding.ObservableInt
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.databinding.ControllerSetupGuideAllowLocationBinding
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.dialogs.CallHelpDialogController
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideLocationSGC4UiModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject

class SetupGuideLocationSGC4Controller :
  CustomToolbarController<ControllerSetupGuideAllowLocationBinding>(), SetupGuideLocationSGC4View,
  CallHelpDialogController.Listener {

  private val viewModel: SetupGuideLocationSGC4ViewModel by inject()
  private var callHelpDialogController: CallHelpDialogController? = null

  val position: Int = 0
  val totalCount: Int = 0
  val showQuestionFab: Boolean = true

  val buttonText = ObservableInt(R.string.guide_ble_off)

  private var locationEnabled = true

  override fun attachVariables(viewBinding: ControllerSetupGuideAllowLocationBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerSetupGuideAllowLocationBinding) {
    super.onViewBound(viewBinding)
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<SetupGuideLocationSGC4UiModel>, Disposable> {
    return RxUi.ui {
      when ((it as SetupGuideLocationSGC4UiModel.CurrentBluetoothState).state) {
        BluetoothState.LOCATION_SERVICES_NOT_ENABLED -> {
          locationEnabled = false
          buttonText.set(R.string.guide_ble_off)
        }
        else -> {
          locationEnabled = true
          buttonText.set(R.string.generic_continue)
        }
      }

    }
  }

  fun onNextClicked() {
    if (locationEnabled) {
      getListener(FlowListener::class.java)?.onNextClicked()
    }
    else {
      Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).also {
        startActivity(it)
      }
    }
  }

  fun onQuestionClicked() {
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

  override fun getLayout(): Int {
    return R.layout.controller_setup_guide_allow_location
  }

  companion object {
    private const val CONTROLLER_TAG = "controller_setup_guide_location_sgc4"
    private const val DIALOG_CALL_HELP = 1005

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SetupGuideLocationSGC4Controller())
        .tag(CONTROLLER_TAG)
    }
  }
}