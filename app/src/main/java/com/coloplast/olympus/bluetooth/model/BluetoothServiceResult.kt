package com.coloplast.olympus.bluetooth.model

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function

interface BluetoothServiceResult<T: BluetoothServiceModel> {
  fun onResult(): Function<Observable<T>, Disposable>
}