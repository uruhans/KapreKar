package com.coloplast.olympus.ui.common.listeners

import android.view.View
import android.widget.AdapterView

interface AdapterSelectedItemListenerImpl : AdapterView.OnItemSelectedListener {
  override fun onNothingSelected(parent: AdapterView<*>?) {
  }
  
  override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
  }
}