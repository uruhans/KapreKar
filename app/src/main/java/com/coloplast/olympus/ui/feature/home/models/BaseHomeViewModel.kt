package com.coloplast.olympus.ui.feature.home.models

import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.feature.home.HomeView
import io.reactivex.disposables.Disposable

abstract class BaseHomeViewModel : BaseViewModel<HomeUiModel, HomeView>() {
  
  abstract fun bindScanner(uiView: HomeView): Disposable

}