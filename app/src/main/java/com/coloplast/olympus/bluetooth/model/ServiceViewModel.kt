package com.coloplast.olympus.bluetooth.model

import io.reactivex.disposables.Disposable

abstract class ServiceViewModel<T: BluetoothServiceModel, V: BluetoothServiceResult<T>> {
  abstract fun bind(serviceResult: V) : Disposable
}