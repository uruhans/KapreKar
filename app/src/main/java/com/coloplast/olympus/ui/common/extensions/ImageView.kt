package com.coloplast.olympus.ui.common.extensions

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter

@BindingAdapter("resource")
fun ImageView.setResource(@DrawableRes resource: Int?) {
  resource?.let {
    setImageResource(it)
  }
}