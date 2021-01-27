package com.coloplast.olympus.ui.feature.uistates

import androidx.core.view.doOnPreDraw
import androidx.core.view.postDelayed
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.models.Battery
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.databinding.ControllerStateWelcomeBinding
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.GenericView
import com.coloplast.olympus.ui.feature.home.models.LastCheckVisible
import com.coloplast.olympus.ui.feature.home.models.PatchVisible
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject
import java.time.LocalDateTime

class WelcomeController : BaseHomeUIControllerAppTour<ControllerStateWelcomeBinding>(), PatchVisible, LastCheckVisible, GenericView {

  private val viewModel: WelcomeViewModel by inject()

  val isConnected = ObservableBoolean(true)
  val patchInErrorState = ObservableBoolean(false)
  val battery = ObservableField<Battery>()
  val lastCheck = ObservableField<LocalDateTime>()

  private val preferences: Preferences by inject()
  private var appTourStarted = false

  override fun attachVariables(viewBinding: ControllerStateWelcomeBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this

    viewBinding.root.doOnPreDraw { closeAfterPeriod(viewBinding) }
  }

  override fun onViewBound(viewBinding: ControllerStateWelcomeBinding) {
    preferences.didSeeWelcome = true
    super.onViewBound(viewBinding)
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui { uiModel ->
      when (uiModel) {
        is GenericUiModel.Success<*> -> {
          val data = uiModel.data as StateChange
          data.bleDevice.peripheral?.battery.let {
            battery.set(it)
            lastCheck.set(LocalDateTime.now())
          }
        }
      }
    }
  }

  override fun lastCheckVisible(): Boolean {
    return true
  }

  private fun closeAfterPeriod(viewBinding: ControllerStateWelcomeBinding) {
    viewBinding.root.postDelayed(MAX_VISIBLE_TIME) {
      router.popController(this)
    }
  }

  override fun startAppTour() {
    appTourStarted = true
    super.startAppTour()
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_welcome
  }

  companion object {
    private const val TAG_CONTROLLER = "state_welcome_controller"
    private const val MAX_VISIBLE_TIME = 5000L
    fun routerTransaction() = RouterTransaction.with(WelcomeController()).tag(TAG_CONTROLLER)
  }
}