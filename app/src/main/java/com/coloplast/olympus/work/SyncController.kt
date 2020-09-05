package com.coloplast.olympus.work

import android.widget.Toast
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerSyncTestBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject

class SyncController : BaseController<ControllerSyncTestBinding>(), SyncView {

  private val viewModel: SyncViewModel by inject()
  private val beginSyncRelay = PublishRelay.create<Unit>()

  override fun attachVariables(viewBinding: ControllerSyncTestBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerSyncTestBinding) {
    compositeDisposable.add(viewModel.bind(this))
  }

  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui {

      when (it) {
        is GenericUiModel.Success<*> -> {
          Toast.makeText(applicationContext, "Yay", Toast.LENGTH_SHORT).show()
        }
        is GenericUiModel.Error -> {
          Toast.makeText(applicationContext, "Errors occurred", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  fun onButtonClicked() {
    beginSyncRelay.accept(Unit)
  }

  override fun beginSync(): Observable<Unit> {
    return beginSyncRelay
      .publish()
      .refCount()
  }

  override fun getLayout(): Int {
    return R.layout.controller_sync_test
  }

  companion object {
    private const val TAG_CONTROLLER = "sync_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SyncController()).tag(TAG_CONTROLLER)
    }
  }
}