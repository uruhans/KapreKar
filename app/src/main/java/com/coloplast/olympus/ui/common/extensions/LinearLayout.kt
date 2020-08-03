package com.coloplast.olympus.ui.common.extensions

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.coloplast.olympus.databinding.ItemConnectedBleDeviceBinding
import com.coloplast.olympus.ui.feature.home.models.ConnectedBleDevice

@BindingAdapter("connectedDevices")
fun LinearLayout.connectedDevices(connectedDevices: ArrayList<ConnectedBleDevice>?) {
  if (connectedDevices.isNullOrEmpty()) {
    return
  }
  
  removeAllViews()
  
  val inflater = LayoutInflater.from(context)
  
  connectedDevices.forEach {
    val viewBinding = ItemConnectedBleDeviceBinding.inflate(inflater, this, false)
    viewBinding.item = it
    addView(viewBinding.root)
  }
}