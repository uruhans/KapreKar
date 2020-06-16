package com.coloplast.olympus.ui.common.widgets

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.listeners.AnimatorListenerImpl
import timber.log.Timber

class ConnectionStateTextView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
  
  init {
    alpha = 0f
    visibility = View.INVISIBLE
  }
  
  override fun setText(text: CharSequence?, type: BufferType?) {
    if(text != this.text && text?.isNotEmpty() == true) {
      super.setText(text, type)
    }
    else {
      // we have to do this - because not doing it caused weird crashes on *some* devices
      super.setText("", type)
    }
  }
  
  fun animateText(@StringRes textRes: Int) {
    animateText(context.getString(textRes))
  }
  
  fun animateText(newText: String) {
    if(newText.isNotEmpty()) {
      animateTextIn(newText)
    }
  }
  
  private fun animateTextIn(newText: String) {
    val animation = AnimatorSet()
  
    val textAnimIn = ObjectAnimator.ofFloat(this, View.TRANSLATION_X, 0f, context.resources.getDimensionPixelSize(R.dimen.spacing_half).toFloat())
    val textAnimAlphaIn = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f)
  
    animation.duration = 700
    animation.startDelay = 200
    
    animation.addListener(object: AnimatorListenerImpl {
      override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
        text = newText
        visibility = View.VISIBLE
      }
  
      override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
        animateTextOut()
      }
    })
    
    animation.playTogether(textAnimIn, textAnimAlphaIn)
    animation.start()
  }
  
  private fun animateTextOut() {
    val textAnimAlphaIn = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f)
  
    textAnimAlphaIn.addListener(object: AnimatorListenerImpl {
      override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
        visibility = View.INVISIBLE
      }
    })
  
    textAnimAlphaIn.startDelay = 2000
    textAnimAlphaIn.duration = 500
    textAnimAlphaIn.start()
  }
}