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
import com.coloplast.olympus.ui.feature.home.models.ScanResultItem
import com.coloplast.olympus.ui.feature.transmitters.add.at9.model.SearchTransmitterUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.koin.core.inject
import timber.log.Timber

class AddTransmitterAT9Controller(args: Bundle) : BaseController<ControllerAddTransmitterAt9Binding>(args), AddTransmitterAT9View, ScanResultItem.Listener {

  enum class ViewState {
    SEARCHING,
    MULTIPLE_RESULTS
    // no need for a NOTHING as it will show a dialog controller
  }

  private val viewModel: AddTransmitterAT9ViewModel by inject()
  private val scanResultRelay = PublishRelay.create<ScanResult>()

  val items = ObservableArrayList<ScanResultItem>()
  val itemBinding: OnItemBindClass<ScanResultItem> = OnItemBindClass<ScanResultItem>()
          .map(ScanResultItem::class.java, BR.item, R.layout.item_scan_result)

  override val transmitterToSearchFor = args.getString(BUNDLE_TRANSMITTER_NAME_TO_SEARCH)!!

  val currentState = ObservableField(ViewState.SEARCHING)

  // this boolean is a little bit of a hack
  // when trying to connect (and bond) to a transmitter (and failing)
  // connection state actually goes into CONNECTED - and then going to DISCONNECTED
  private var connectionAlreadyFailed = false

  override fun attachVariables(viewBinding: ControllerAddTransmitterAt9Binding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerAddTransmitterAt9Binding) {
    compositeDisposable.add(viewModel.bind(this))
  }
  
  override fun render(): Function<Observable<SearchTransmitterUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      Timber.d("$uiModel")
      
      when(uiModel) {
        is SearchTransmitterUiModel.TransmittersFound -> {
          if(uiModel.scanResults.size > 1) {
            // we have more than one result, so show result set to the user to allow him to choose
            uiModel.scanResults.forEach { newScanResult ->
              if (items.none { item -> item.scanResult.macAddress == newScanResult.macAddress }) {
                items.add(ScanResultItem(newScanResult, this))
              }
            }

            currentState.set(ViewState.MULTIPLE_RESULTS)
          }
          else {
            // we only have one result, so try to connect to this one
            onScanResultClicked(uiModel.scanResults.first())
          }
        }
        is SearchTransmitterUiModel.NothingFound -> {
          // TODO - show nothing found popup
        }
        is SearchTransmitterUiModel.ConnectionSuccessful -> {
          // skip any result coming here if we've already failed once before
          // TODO - find a better way of handling this - preferably in the viewmodel
          if(connectionAlreadyFailed) {
            return@ui
          }

          // TODO - move to next screen (connection is established)
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

  override fun onScanResultClicked(scanResult: ScanResult) {
    connectionAlreadyFailed = false
    scanResultRelay.accept(scanResult)
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_add_transmitter_at_9
  }
  
  companion object {
    private const val CONTROLLER_TAG = "add_transmitter_at9_controller"
    private const val BUNDLE_TRANSMITTER_NAME_TO_SEARCH = "BUNDLE_TRANSMITTER_NAME_TO_SEARCH"
    
    fun routerTransaction(transmitterName: String) : RouterTransaction {
      with(Bundle()) {
        putString(BUNDLE_TRANSMITTER_NAME_TO_SEARCH, transmitterName)
        
        return RouterTransaction.with(AddTransmitterAT9Controller(this)).tag(CONTROLLER_TAG)
      }
    }
  }
}