package com.coloplast.olympus.ui.common.models

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function

interface UiView<T : UiModel> {
  fun render(): Function<Observable<T>, Disposable>
}