package com.coloplast.olympus.ui.feature.onboarding.setup.applying

import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAwaitingConnectionBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.extensions.rotate360
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.feature.onboarding.setup.model.AwaitingConnectionUiModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject

class AwaitingConnectionController : BaseController<ControllerAwaitingConnectionBinding>(),
  TopMenuVisibilityListener, AwaitingConnectionView {

  private val viewModel: AwaitingConnectionViewModel by inject()

  override fun attachVariables(viewBinding: ControllerAwaitingConnectionBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerAwaitingConnectionBinding) {
    viewBinding.appLogo.rotate360(animationDuration = 4000, loopAnimation = true)

    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<AwaitingConnectionUiModel>, Disposable> {
    return RxUi.ui {
      getListener(FlowListener::class.java)?.onNextClicked(it.isAttached)
    }
  }

  override fun showBackButton(): Boolean {
    return false
  }

  override fun showCloseButton(): Boolean {
    return false
  }

  override fun getLayout(): Int {
    return R.layout.controller_awaiting_connection
  }

  companion object {
    private const val TAG_CONTROLLER = "controller_awaiting_connection"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(AwaitingConnectionController()).tag(TAG_CONTROLLER)
    }
  }

}