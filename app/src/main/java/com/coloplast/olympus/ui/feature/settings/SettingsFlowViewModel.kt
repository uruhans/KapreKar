package com.coloplast.olympus.ui.feature.settings

import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class SettingsFlowViewModel(): BaseViewModel<GenericUiModel, SettingsFlowView>() {
  override fun bind(uiView: SettingsFlowView): Disposable {
    val disposable = CompositeDisposable()

    return disposable
  }
}