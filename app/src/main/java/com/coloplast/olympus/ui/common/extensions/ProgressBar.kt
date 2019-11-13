package com.coloplast.olympus.ui.common.extensions

import android.animation.ObjectAnimator
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter

@BindingAdapter("animateProgress")
fun ProgressBar.animateProgress(newProgress: Int) {
  val progressAnimator = ObjectAnimator.ofInt(this, "progress", progress, newProgress)
  progressAnimator.duration = 500
  progressAnimator.start()
}