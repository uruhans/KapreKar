package com.coloplast.olympus.ui.common.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
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
  animateFade(this, if(visible) View.VISIBLE else View.INVISIBLE)
}

@BindingAdapter("visibleOrGoneFade")
fun View.visibleOrGoneFade(visible: Boolean) {
  animateFade(this, if(visible) View.VISIBLE else View.GONE)
}

private fun animateFade(target: View, endResult: Int) {
  val visible = endResult == View.VISIBLE
  
  val animator = ObjectAnimator.ofFloat(target, View.ALPHA, if(visible) 1f else 0f)
  animator.duration = 1500
  
  animator.addListener(object: AnimatorListenerImpl {
    override fun onAnimationEnd(animation: Animator?) {
      if(visible.not()) {
        target.visibility = endResult
      }
    }
    
    override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
      if(visible) {
        target.visibility = View.VISIBLE
      }
    }
  })
  
  target.visibility = if(visible) View.VISIBLE else endResult
  animator.start()
}

fun View.hideKeyboard() {
  val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}