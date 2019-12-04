package com.coloplast.olympus.ui.common.extensions

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("visibleOrInvisible")
fun View.visibleOrInvisible(visible: Boolean) {
  visibility = if(visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(visible: Boolean) {
  visibility = if(visible) View.VISIBLE else View.GONE
}