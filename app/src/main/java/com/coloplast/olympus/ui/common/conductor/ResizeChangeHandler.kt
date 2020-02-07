package com.coloplast.olympus.ui.common.conductor

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.ui.common.listeners.AnimatorListenerImpl
import com.coloplast.olympus.utils.UiUtils

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
        else if(it is CardView) {
          newHeight += AppSettings.applicationContext.resources.getDimensionPixelSize(R.dimen.cardview_button_height_default)
        }
        
        newHeight += it.marginTop.plus(it.marginBottom)
  
        it.visibility = View.GONE
      }
  
      val objectAnimator = ObjectAnimator.ofInt(from.height, newHeight)
  
      objectAnimator.addUpdateListener {
        val params = container.layoutParams
        params.height = it.animatedValue as Int
        container.layoutParams = params
      }
      
      objectAnimator.startDelay = 200
      objectAnimator.duration = 400
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