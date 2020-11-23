package com.coloplast.olympus.ui.feature.transmitters.add.at7

import android.os.Bundle
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.databinding.ControllerMediaGuideBinding
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.feature.transmitters.add.at7.model.AddTransmitterAT7UiModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject


class AddTransmitterAT7Controller(args: Bundle = Bundle()) :
  MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }),
  AddTransmitterAT7View {

  private val viewModel: AddTransmitterAT7ViewModel by inject()

  override val looping: Boolean = true
  override val resourceId: Int = R.raw.at7_plug_in_charger
  override val titleId: Int = R.string.guide_at7_title
  override val descriptionId: Int = R.string.guide_at7_body
  override val position: Int = 4
  override val totalCount: Int = 5
  override var nextEnabled: Boolean = true
  override var showBackButton: Boolean = true
  override var showCloseButton: Boolean = false

  var bluetoothEnabled = true

  override fun onViewBound(viewBinding: ControllerMediaGuideBinding) {
    super.onViewBound(viewBinding)
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<AddTransmitterAT7UiModel>, Disposable> {
    return RxUi.ui {

      bluetoothEnabled = it.bluetoothState != BluetoothState.BLUETOOTH_NOT_ENABLED
    }
  }

  override fun onNextClicked() {
    getListener(FlowListener::class.java)?.onNextClicked(bluetoothEnabled)
  }

  companion object {
    private const val TAG_CONTROLLER = "add_transmitter_at7_controller"

    fun routerTransaction() = RouterTransaction.with(AddTransmitterAT7Controller())
      .tag(TAG_CONTROLLER)
  }
}