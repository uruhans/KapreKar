package com.coloplast.olympus.ui.common.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import androidx.databinding.BindingAdapter
import com.coloplast.olympus.ui.common.listeners.AnimatorListenerImpl

@BindingAdapter("visibleOrInvisible")
fun View.visibleOrInvisible(visible: Boolean) {
  visibility = if(visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("visibleOrGone")
fun View.visibleOrGone(visible: Boolean) {
  visibility = if(visible) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleOrInvisibleFade")
fun View.visibleOrInvisibleFade(visible: Boolean) {
  visibility = if(visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("visibleOrGoneFade")
fun View.visibleOrGoneFade(visible: Boolean) {
  
  val animator = ObjectAnimator.ofFloat(this, View.ALPHA, if(visible) 1f else 0f)
  animator.addListener(object: AnimatorListenerImpl {
    override fun onAnimationEnd(animation: Animator?) {
      if(visible.not()) {
        visibility = View.GONE
      }
    }
  
    override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
      if(visible) {
        visibility = View.VISIBLE
      }
    }
  })
  
  visibility = if(visible) View.VISIBLE else View.GONE
  animator.start()
}