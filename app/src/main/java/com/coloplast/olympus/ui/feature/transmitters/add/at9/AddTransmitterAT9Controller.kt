package com.coloplast.olympus.ui.feature.transmitters.add.at9

import android.os.Bundle
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableInt
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.databinding.ControllerAddTransmitterAt9Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.home.models.ScanResultItem
import com.coloplast.olympus.ui.feature.transmitters.add.at9.model.SearchTransmitterUiModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.koin.core.inject
import timber.log.Timber

class AddTransmitterAT9Controller(args: Bundle) : BaseController<ControllerAddTransmitterAt9Binding>(args), AddTransmitterAT9View, ScanResultItem.Listener {
  
  private val viewModel: AddTransmitterAT9ViewModel by inject()

  val items = ObservableArrayList<ScanResultItem>()
  val itemBinding: OnItemBindClass<ScanResultItem> = OnItemBindClass<ScanResultItem>()
          .map(ScanResultItem::class.java, BR.item, R.layout.item_scan_result)

  private val transmitterToSearchFor = args.getString(BUNDLE_TRANSMITTER_NAME_TO_SEARCH)!!

  val currentState = ObservableInt(0)

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

          uiModel.scanResults.forEach { newScanResult ->
            if (items.none { item -> item.scanResult.macAddress == newScanResult.macAddress }) {
              items.add(ScanResultItem(newScanResult, this))
            }
          }

          currentState.set(1);

        }
        is SearchTransmitterUiModel.NothingFound -> {
        
        }
        is SearchTransmitterUiModel.PendingUser -> {
        
        }
      }
    }
  }

  override fun onScanResultClicked(scanResult: ScanResult) {
    Timber.e("scanresult = ${scanResult.macAddress}")
  }

  override fun getTransmitterName(): String {
    return transmitterToSearchFor
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