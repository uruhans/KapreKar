package com.coloplast.olympus.ui.common.conductor

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.listeners.AnimatorListenerImpl

class ResizeChangeHandler : AnimatorChangeHandler() {
  
  override fun getAnimator(container: ViewGroup, from: View?, to: View?, isPush: Boolean, toAddedToContainer: Boolean): Animator {
    val animator = AnimatorSet()
    val viewAnimators: MutableList<Animator> = mutableListOf()
    
    if (to != null && from != null) {
      var newHeight = 0
      
      (to as? ViewGroup)?.children?.forEach {
        when (it) {
          is TextView -> {
            newHeight += it.lineHeight.times(it.lineCount)
          }
          is CardView -> {
            newHeight += it.context.resources.getDimensionPixelSize(R.dimen.cardview_button_height_default)
          }
          is ImageView -> {
            newHeight += it.context.resources.getDimensionPixelSize(R.dimen.imageview_height_default)
          }
        }
        
        newHeight += it.marginTop.plus(it.marginBottom)
        it.visibility = View.INVISIBLE
      }
  
      val heightAnimator = ObjectAnimator.ofInt(from.height, newHeight)
  
      heightAnimator.addUpdateListener {
        val params = container.layoutParams
        params.height = it.animatedValue as Int
        container.layoutParams = params
      }
  
      heightAnimator.startDelay = 200
      heightAnimator.duration = 400
      viewAnimators.add(heightAnimator)
  
      val textAnimIn = ObjectAnimator.ofFloat(to, View.TRANSLATION_Y, to.height.toFloat(), 0f)
      textAnimIn.addListener(object : AnimatorListenerImpl {
        override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
          (to as? ViewGroup)?.children?.forEach {
            it.visibility = View.VISIBLE
          }
        }
      })
      textAnimIn.startDelay = 600
      textAnimIn.duration = 300

      viewAnimators.add(textAnimIn)
      
      val textAnimAlphaIn = ObjectAnimator.ofFloat(to, View.ALPHA, 0f, 1f)
      textAnimAlphaIn.duration = 300
      textAnimAlphaIn.startDelay = 600
  
      viewAnimators.add(textAnimAlphaIn)
      
      val textAnimOut = ObjectAnimator.ofFloat(from, View.ALPHA, 1f, 0f)
      textAnimOut.duration = 200
      textAnimOut.addListener(object: AnimatorListenerImpl {
        override fun onAnimationEnd(animation: Animator?) {
          (from as? ViewGroup)?.children?.forEach {
            it.visibility = View.INVISIBLE
          }
        }
      })
      
      viewAnimators.add(textAnimOut)
    }
    
    animator.playTogether(viewAnimators)
    return animator
  }
  
  override fun resetFromView(from: View) { }
}