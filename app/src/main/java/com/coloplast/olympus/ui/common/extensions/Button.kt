package com.coloplast.olympus.ui.common.extensions

import android.widget.Button
import androidx.databinding.BindingAdapter
import com.coloplast.olympus.utils.UiUtils
import timber.log.Timber

@BindingAdapter("safeText")
fun Button.setBindText(value: Any) {
  when (value) {
    is CharSequence -> text = value
    is Int -> {
      if (value != 0 && value != UiUtils.INVALID_INT) {
        text = context.getString(value)
      }
      else {
        Timber.w("Trying to set an invalid text resource [$value] on a Button - is this on purpose?")
      }
    }
    else -> Timber.w("Trying to set an invalid text resource [$value] on a Button - is this on purpose?")
  }
}