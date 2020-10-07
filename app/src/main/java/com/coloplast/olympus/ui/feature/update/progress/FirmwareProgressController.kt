package com.coloplast.olympus.ui.feature.update.progress

import android.os.Bundle
import androidx.databinding.ObservableInt
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerFirmwareUpdateProgressBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.feature.update.progress.model.UpdateModel
import com.coloplast.olympus.utils.DeviceUtils
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject

class FirmwareProgressController(args: Bundle) : BaseController<ControllerFirmwareUpdateProgressBinding>(args), FirmwareProgressView {

  private val viewModel: FirmwareProgressViewModel by inject()
  private val macAddressRelay = PublishRelay.create<UpdateModel>()

  private val macAddress = args.getString(BUNDLE_MACADDRESS)!!

  val titleRes = ObservableInt(R.string.guide_update_transmitter_3_title1)
  val currentProgress = ObservableInt(0)
  val maxProgress = 125

  override fun attachVariables(viewBinding: ControllerFirmwareUpdateProgressBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerFirmwareUpdateProgressBinding) {
    compositeDisposable.add(viewModel.bind(this))

    // initiate firmware update
    macAddressRelay.accept(UpdateModel(macAddress, DeviceUtils.versionName()))
  }

  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui {

      when(it) {
        is GenericUiModel.Success<*> -> {
          val progress = it.data as Int

          if(progress >= 25 && titleRes.get() != R.string.guide_update_transmitter_3_title2) {
            titleRes.set(R.string.guide_update_transmitter_3_title2)
          }

          currentProgress.set(progress)
        }
        is GenericUiModel.Error -> {
          // 500 -> InvalidSignatureException
          // 1000 -> other errors
        }
      }
    }
  }

  override fun updateFirmwareRequested(): Observable<UpdateModel> {
    return macAddressRelay
      .publish()
      .refCount()
  }

  override fun getLayout(): Int {
    return R.layout.controller_firmware_update_progress
  }

  companion object {

    private const val BUNDLE_MACADDRESS = "bundle_macaddress"

    fun routerTransaction(macAddress: String) : RouterTransaction {
      return with(Bundle()) {
        putString(BUNDLE_MACADDRESS, macAddress)
        return@with RouterTransaction.with(FirmwareProgressController(this))
      }
    }

  }
}