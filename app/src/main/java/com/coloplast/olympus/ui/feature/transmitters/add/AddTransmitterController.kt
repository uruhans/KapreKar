package com.coloplast.olympus.ui.feature.transmitters.add

import android.os.Bundle
import androidx.databinding.ObservableArrayList
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.databinding.ControllerTransmittersAddBinding
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.feature.home.models.ScanResultItem
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass
import org.koin.core.inject

class AddTransmitterController(args: Bundle? = null) : CustomToolbarController<ControllerTransmittersAddBinding>(args), AddTransmitterView,
    ScanResultItem.Listener {
  
  private val viewModel: AddTransmitterViewModel by inject()
  
  val items = ObservableArrayList<ScanResultItem>()
  val itemBinding: OnItemBindClass<ScanResultItem> = OnItemBindClass<ScanResultItem>()
    .map(ScanResultItem::class.java, BR.item, R.layout.item_scan_result)
  
  private val scanRequestRelay = PublishRelay.create<Unit>()
  private val addTransmitterRelay = PublishRelay.create<ScanResult>()
  
  override fun attachVariables(viewBinding: ControllerTransmittersAddBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerTransmittersAddBinding) {
    super.onViewBound(viewBinding)
    compositeDisposable.add(viewModel.bind(this))
    
    scanRequestRelay.accept(Unit)
  }
  
  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui { uiModel ->
    
      when(uiModel) {
        is GenericUiModel.Success<*> -> {
          
          if(uiModel.data is ScanResult) {
            if (!items.any { it.scanResult.macAddress == uiModel.data.macAddress }) {
              items.add(ScanResultItem(uiModel.data, this))
            }
          }
          else {
            handleBack()
          }
        }
      }
    }
  }
  
  override fun onScanResultClicked(scanResult: ScanResult) {
    addTransmitterRelay.accept(scanResult)
  }
  
  override fun onScanRequested(): Observable<Unit> {
    return scanRequestRelay.publish().refCount()
  }
  
  override fun onAddTransmitter(): Observable<ScanResult> {
    return addTransmitterRelay.publish().refCount()
  }
  
  override fun getNavigationIconResId(): Int {
    return R.drawable.ic_arrow
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_transmitters_add
  }
  
  companion object {
    private const val TAG_CONTROLLER = "add_transmitter_controller"
    
    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(AddTransmitterController())
        .tag(TAG_CONTROLLER)
    }
  }
}