package com.coloplast.olympus.ui.feature.update.progress

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.ObservableInt
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerFirmwareUpdateProgressBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.feature.update.progress.model.FirmwareProgressUiModel
import com.coloplast.olympus.ui.feature.update.progress.model.UpdateModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject
import timber.log.Timber

class FirmwareProgressController(args: Bundle) : BaseController<ControllerFirmwareUpdateProgressBinding>(args), FirmwareProgressView {

  private val viewModel: FirmwareProgressViewModel by inject()
  private val macAddress = args.getString(BUNDLE_MACADDRESS)!!

  private val updatingStringRes = R.string.guide_update_transmitter_3_title2
  private val model = UpdateModel(macAddress)

  val titleRes = ObservableInt(R.string.guide_update_transmitter_3_title1)
  val currentProgress = ObservableInt(0)

  override fun attachVariables(viewBinding: ControllerFirmwareUpdateProgressBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerFirmwareUpdateProgressBinding) {
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<FirmwareProgressUiModel>, Disposable> {
    return RxUi.ui {

      when(it) {
        is FirmwareProgressUiModel.ProgressUpdate -> {
          if(it.currentProgress >= 25 && titleRes.get() != updatingStringRes) {
            titleRes.set(updatingStringRes)
          }

          Timber.d("Total Progress: [${it.currentProgress}]")
          currentProgress.set(it.currentProgress)
        }
        is FirmwareProgressUiModel.InvalidFirmwareSignature,
        is FirmwareProgressUiModel.Error -> {
          Toast.makeText(activity!!, "InvalidSignature", Toast.LENGTH_SHORT).show()
        }
        is FirmwareProgressUiModel.UpdateCompleted -> {
          getListener(FlowListener::class.java)?.onNextClicked()
        }
      }
    }
  }

  override fun getUpdateModel(): UpdateModel {
    return model
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