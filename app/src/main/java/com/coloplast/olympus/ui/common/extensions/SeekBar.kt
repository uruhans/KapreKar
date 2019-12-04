package com.coloplast.olympus.ui.common.extensions

import android.widget.SeekBar
import androidx.databinding.BindingAdapter

@BindingAdapter("overridePadding")
fun SeekBar.overridePadding(newPadding: Int) {
  this.setPadding(newPadding, newPadding, newPadding, newPadding)
}