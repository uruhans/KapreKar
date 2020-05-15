package com.coloplast.olympus.ui.feature.transmitters.add.at9

import android.os.Bundle
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.databinding.ControllerAddTransmitterAt9Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.dialogs.ImageDialogController
import com.coloplast.olympus.ui.feature.home.models.ScanResultItem
import com.coloplast.olympus.ui.feature.transmitters.add.at9.model.SearchTransmitterUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.koin.core.inject
import timber.log.Timber

class AddTransmitterAT9Controller(args: Bundle) :
  BaseController<ControllerAddTransmitterAt9Binding>(args), AddTransmitterAT9View,
  ScanResultItem.Listener,
  ImageDialogController.Listener {

  enum class ViewState {
    SEARCHING,
    MULTIPLE_RESULTS
    // no need for a NOTHING as it will show a dialog controller
  }

  enum class DialogClicked {
    CLOSE,
    RETRY,
    NONE
  }

  private val viewModel: AddTransmitterAT9ViewModel by inject()
  private val scanResultRelay = PublishRelay.create<ScanResult>()
  private val retryScanRelay = PublishRelay.create<Unit>()

  val items = ObservableArrayList<ScanResultItem>()
  val itemBinding: OnItemBindClass<ScanResultItem> = OnItemBindClass<ScanResultItem>()
    .map(ScanResultItem::class.java, BR.item, R.layout.item_scan_result)

  private val transmitterToSearchFor = args.getString(BUNDLE_TRANSMITTER_NAME_TO_SEARCH)!!

  private var userClickState = DialogClicked.NONE

  val currentState = ObservableField(ViewState.SEARCHING)

  // this boolean is a little bit of a hack
  // when trying to connect (and bond) to a transmitter (and failing)
  // connection state actually goes into CONNECTED - and then going to DISCONNECTED
  private var connectionAlreadyFailed = false

  private var imageDialogController: ImageDialogController? = null

  override fun attachVariables(viewBinding: ControllerAddTransmitterAt9Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerAddTransmitterAt9Binding) {
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<SearchTransmitterUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      Timber.d("$uiModel")

      when (uiModel) {
        is SearchTransmitterUiModel.TransmittersFound -> {

          // try to see if *out* transmitter is amongst the scan results
          uiModel.scanResults.find { it.name?.contains(transmitterToSearchFor) ?: false }.let {
            if (it != null && uiModel.scanResults.size == 1) {
              // we found it, so try to connect to it
              onScanResultClicked(it)
            } else {
              // we have more than one result, so show result set to the user to allow him to choose
              uiModel.scanResults.forEach { newScanResult ->
                if (items.none { item -> item.scanResult.macAddress == newScanResult.macAddress }) {
                  items.add(ScanResultItem(newScanResult, this))
                }
              }

              currentState.set(ViewState.MULTIPLE_RESULTS)
            }
          }
        }
        is SearchTransmitterUiModel.NothingFound -> {
          imageDialogController = ImageDialogController.create(
            this,
            DIALOG_NO_TRANSMITTERS,
            R.string.dialog_transmitters_not_available_title,
            R.string.dialog_transmitters_not_available_message,
            R.string.dialog_transmitters_not_available_search_again_button,
            verticalButtonLayout = true,
            topImage = R.drawable.ic_info_circle,
            dismissAllowed = false
          )

          imageDialogController?.show()
        }
        is SearchTransmitterUiModel.ConnectionSuccessful -> {
          // skip any result coming here if we've already failed once before
          if (connectionAlreadyFailed) {
            return@ui
          }

          // this assumes the flow controller implements the FlowListener
          getListener(FlowListener::class.java)?.onNextClicked()
        }
        is SearchTransmitterUiModel.ConnectionFailure -> {
          connectionAlreadyFailed = true
          // TODO - possibly show failure to connect (possibly because bonding issues seems to be automatically shown to the user)
        }
      }
    }
  }

  override fun scanResultToConnect(): Observable<ScanResult> {
    return scanResultRelay.publish().refCount()
  }

  override fun retryTransmitterSearchObservable(): Observable<Unit> {
    return retryScanRelay.publish().refCount()
  }

  override fun onScanResultClicked(scanResult: ScanResult) {
    connectionAlreadyFailed = false
    scanResultRelay.accept(scanResult)
  }

  override fun onPositiveButtonClicked(requestCode: Int) {
    when (requestCode) {
      DIALOG_NO_TRANSMITTERS -> {
        userClickState = DialogClicked.RETRY
        imageDialogController?.dismissDialog(true)
      }
    }
  }

  override fun onCloseClicked(requestCode: Int) {
    when (requestCode) {
      DIALOG_NO_TRANSMITTERS -> {
        userClickState = DialogClicked.CLOSE
        imageDialogController?.dismissDialog(true)
      }
    }
  }

  override fun onDialogDismissed(requestCode: Int) {
    when (requestCode) {
      DIALOG_NO_TRANSMITTERS -> {
        if (userClickState == DialogClicked.RETRY) {
          retryScanRelay.accept(Unit)
        }
        else if (userClickState == DialogClicked.CLOSE) {
          // this assumes the flow controller implements the FlowListener
          getListener(FlowListener::class.java)?.onPreviousClicked()
        }
      }
    }
  }

  override fun getLayout(): Int {
    return R.layout.controller_add_transmitter_at_9
  }

  companion object {
    private const val CONTROLLER_TAG = "add_transmitter_at9_controller"
    private const val BUNDLE_TRANSMITTER_NAME_TO_SEARCH = "BUNDLE_TRANSMITTER_NAME_TO_SEARCH"

    private const val DIALOG_NO_TRANSMITTERS = 4040

    fun routerTransaction(transmitterName: String): RouterTransaction {
      with(Bundle()) {
        putString(BUNDLE_TRANSMITTER_NAME_TO_SEARCH, transmitterName)

        return RouterTransaction.with(AddTransmitterAT9Controller(this)).tag(CONTROLLER_TAG)
      }
    }
  }
}