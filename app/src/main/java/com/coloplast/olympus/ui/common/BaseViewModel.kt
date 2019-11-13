package com.coloplast.olympus.ui.common

import com.coloplast.olympus.ui.common.models.UiModel
import com.coloplast.olympus.ui.common.models.UiView
import io.reactivex.disposables.Disposable

abstract class BaseViewModel<T : UiModel, V : UiView<T>> {
  
  abstract fun bind(uiView: V): Disposable
  
}