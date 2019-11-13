package com.coloplast.olympus.ui.common.listeners

import android.animation.Animator

interface AnimatorListenerImpl : Animator.AnimatorListener {
  override fun onAnimationRepeat(animation: Animator?) {}
  
  override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {}
  
  override fun onAnimationEnd(animation: Animator?) {}
  
  override fun onAnimationCancel(animation: Animator?) {}
  
  override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {}
  
  override fun onAnimationStart(animation: Animator?) {}
}