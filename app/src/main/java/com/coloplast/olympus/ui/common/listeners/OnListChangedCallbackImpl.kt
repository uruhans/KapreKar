package com.coloplast.olympus.ui.common.listeners

import androidx.databinding.ObservableList

abstract class OnListChangedCallbackImpl<T> : ObservableList.OnListChangedCallback<ObservableList<T>>() {
  override fun onChanged(sender: ObservableList<T>?) {}
  
  override fun onItemRangeRemoved(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {}
  
  override fun onItemRangeMoved(sender: ObservableList<T>?, fromPosition: Int, toPosition: Int, itemCount: Int) {}
  
  override fun onItemRangeInserted(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {}
  
  override fun onItemRangeChanged(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {}
}