package com.coloplast.olympus.ui.feature.about

import androidx.annotation.StringRes
import androidx.databinding.ObservableField
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAboutBinding
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.DrawerStateDispatcher
import com.coloplast.olympus.ui.common.WebViewController
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.GenericView
import com.coloplast.olympus.utils.DeviceUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject

class AboutController : CustomToolbarController<ControllerAboutBinding>(), GenericView {

  private val viewModel: AboutViewModel by inject()

  val appVersion = ObservableField(DeviceUtils.getAppVersion())

  val nameTx1 = ObservableField<String>()
  val nameTx2 = ObservableField<String>()

  override fun attachVariables(viewBinding: ControllerAboutBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerAboutBinding) {
    super.onViewBound(viewBinding)

    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      when(uiModel) {
        is GenericUiModel.Success<*> -> {
          val txData = uiModel.data as List<Transmitter>

          val firstTx = txData.first()
          val secondTx = txData.lastOrNull { it.id != firstTx.id }

          nameTx1.set("${firstTx.name} (v${firstTx.firmwareVersion})")
          secondTx?.let {
            nameTx2.set("${it.name} (v${it.firmwareVersion})")
          }
        }
      }
    }
  }

  fun onLinkClicked(@StringRes resId: Int) {
    val url = getString(resId).toString()

    getContentRouter().pushController(
      WebViewController.getRouterTransaction(url)
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }

  fun onEmailClicked(@StringRes resId: Int) {
    val email = getString(resId).toString()
  }

  override fun getLayout(): Int = R.layout.controller_about

  override fun getNavigationIconResId(): Int = R.drawable.ic_arrow

  override fun onCloseClicked() {
    DrawerStateDispatcher.closeNavigationDrawerOnHome()
    super.onCloseClicked()
  }

  companion object {

    private const val CONTROLLER_TAG = "about_controller"

    fun routerTransaction() = RouterTransaction.with(AboutController())
      .tag(CONTROLLER_TAG)
  }
}