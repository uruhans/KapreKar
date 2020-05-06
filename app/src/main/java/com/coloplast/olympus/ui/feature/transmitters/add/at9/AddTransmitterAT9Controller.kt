package com.coloplast.olympus.ui.feature.transmitters.add.at9

import android.os.Bundle
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAddTransmitterAt9Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.transmitters.add.at9.model.SearchTransmitterUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject

class AddTransmitterAT9Controller(args: Bundle) : BaseController<ControllerAddTransmitterAt9Binding>(args), AddTransmitterAT9View {
  
  private val viewModel: AddTransmitterAT9ViewModel by inject()
  
  private val transmitterToSearchFor = args.getString(BUNDLE_TRANSMITTER_NAME_TO_SEARCH)!!
  
  override fun attachVariables(viewBinding: ControllerAddTransmitterAt9Binding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerAddTransmitterAt9Binding) {
    compositeDisposable.add(viewModel.bind(this))
  }
  
  override fun render(): Function<Observable<SearchTransmitterUiModel>, Disposable> {
    return RxUi.ui { uiModel ->
      
      when(uiModel) {
        is SearchTransmitterUiModel.TransmitterFound -> {
        
        }
        is SearchTransmitterUiModel.NothingFound -> {
        
        }
        is SearchTransmitterUiModel.PendingUser -> {
        
        }
      }
    }
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