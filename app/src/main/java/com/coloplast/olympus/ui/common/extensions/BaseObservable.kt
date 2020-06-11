package com.coloplast.olympus.ui.common.extensions

import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 11/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 *
 * https://proandroiddev.com/the-ugly-onpropertychangedcallback-63c78c762394
 */

fun BaseObservable.onValueChanged(callback: () -> Unit): Disposable =
  object : Observable.OnPropertyChangedCallback(){
    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
      callback.invoke()
    }
  }.also { addOnPropertyChangedCallback(it) }.let {
    Disposables.fromAction { removeOnPropertyChangedCallback(it) }
  }