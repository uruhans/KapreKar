package com.coloplast.olympus.ui.common.rx

import com.coloplast.olympus.shared.models.ServerException
import com.coloplast.olympus.ui.common.models.GenericUiModel
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import timber.log.Timber
import java.io.IOException

class GenericUiModelTransformer<T> : ObservableTransformer<DelayedLoadingModel<T>, GenericUiModel> {
  
  override fun apply(upstream: Observable<DelayedLoadingModel<T>>): ObservableSource<GenericUiModel> {
    return upstream.map {
      return@map when {
        it.isLoading -> GenericUiModel.Loading(GenericUiModel.Loading.State.INDICATE_LOADING)
        it.result != null -> GenericUiModel.Success(it.result!!)
        else -> GenericUiModel.Loading(GenericUiModel.Loading.State.PROCESSING)
      }
    }
        .onErrorReturn {
          Timber.e("$it")
          
          return@onErrorReturn when (it) {
            is ServerException -> {
              when (it.responseCode) {
                404 -> GenericUiModel.Error(
                  GenericUiModel.Error.State.ERROR_GENERIC,
                  it.responseCode
                )
                else -> GenericUiModel.Error(
                  GenericUiModel.Error.State.ERROR_NETWORK,
                  it.responseCode
                )
              }
            }
            is IOException -> GenericUiModel.Error(GenericUiModel.Error.State.ERROR_NETWORK, 1000)
            else -> GenericUiModel.Error(GenericUiModel.Error.State.ERROR_GENERIC, 1010)
          }
        }
  }
}