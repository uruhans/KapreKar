package com.coloplast.olympus.ui.feature.update.charging


import androidx.databinding.ObservableField
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.enums.BatteryState
import com.coloplast.olympus.databinding.ControllerFirmwareUpdateChargingBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.GenericView
import com.coloplast.olympus.ui.feature.update.charging.model.ChargingStateModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject

class FirmwareUpdateChargingController : BaseController<ControllerFirmwareUpdateChargingBinding>(), TopMenuVisibilityListener, GenericView {

  private val viewModel : FirmwareUpdateChargingViewModel by inject()

  val resourceId = R.drawable.placeholder // TODO change to proper video or image
  val batteryState = ObservableField<BatteryState>(BatteryState.UNKNOWN)
  val macAddress  = ObservableField<String?>("")

  override fun attachVariables(viewBinding: ControllerFirmwareUpdateChargingBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerFirmwareUpdateChargingBinding) {
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui {
      if(it is GenericUiModel.Success<*> && it.data is ChargingStateModel) {
          batteryState.set(it.data.batteryState)
          macAddress.set(it.data.macAddress)
      }
    }
  }

  override fun showBackButton(): Boolean = true

  override fun showCloseButton(): Boolean = false

  fun onContinueClicked()  {

    if (macAddress.get()?.isNotBlank()!!) {
      getListener(FlowListener::class.java)?.onNextClicked(macAddress.get())
    }
  }

  override fun getLayout(): Int = R.layout.controller_firmware_update_charging

  companion object {
    private const val TAG_CONTROLLER = "update_transmitter_update_process"
    fun routerTransaction() = RouterTransaction.with(FirmwareUpdateChargingController()).tag(
      TAG_CONTROLLER
    )
  }


}