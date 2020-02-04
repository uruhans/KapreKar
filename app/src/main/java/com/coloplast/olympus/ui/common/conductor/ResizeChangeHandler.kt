package com.coloplast.olympus.ui.common.conductor

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler
import com.coloplast.olympus.ui.common.listeners.AnimatorListenerImpl

class ResizeChangeHandler : AnimatorChangeHandler() {
  
  override fun getAnimator(container: ViewGroup, from: View?, to: View?, isPush: Boolean, toAddedToContainer: Boolean): Animator {
    val animator = AnimatorSet()
    val viewAnimators: MutableList<Animator> = mutableListOf()
    
    if (to != null && from != null) {
      var newHeight = 0
      
      (to as? ViewGroup)?.children?.forEach {
        if(it is TextView) {
          newHeight += it.lineHeight.times(it.lineCount)
        }
  
        it.visibility = View.GONE
      }
  
      val objectAnimator = ObjectAnimator.ofInt(from.height, newHeight)
  
      objectAnimator.addUpdateListener {
        val params = container.layoutParams
        params.height = it.animatedValue as Int
        container.layoutParams = params
      }
      
      objectAnimator.startDelay = 200
      objectAnimator.duration = 200
      viewAnimators.add(objectAnimator)
  
      val textAnimIn = ObjectAnimator.ofFloat(to, View.TRANSLATION_Y, to.height.toFloat(), 0f)
      textAnimIn.addListener(object : AnimatorListenerImpl {
        override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
          (to as? ViewGroup)?.children?.forEach {
            it.visibility = View.VISIBLE
          }
        }
      })
      textAnimIn.startDelay = 400
      textAnimIn.duration = 200
      
      viewAnimators.add(textAnimIn)
      
      val textAnimOut = ObjectAnimator.ofFloat(from, View.ALPHA, 0f)
      textAnimIn.duration = 100
      viewAnimators.add(textAnimOut)
    }

    
    animator.playTogether(viewAnimators)
    return animator
  }
  
  override fun resetFromView(from: View) {
    from.alpha = 1f
  }
}