package com.coloplast.olympus.ui.common.widgets

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.coloplast.olympus.utils.DeviceUtils

class AnimatedProgressView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener {
  
  private var animationTimeInSeconds = 9
  private val animator: ValueAnimator = ValueAnimator.ofInt(0, DeviceUtils.screenWidth())
  
  init {
    animator.repeatMode = ValueAnimator.RESTART
    animator.duration = animationTimeInSeconds.times(1000L)
    animator.addUpdateListener(this)
  }
  
  fun update(shouldRestart: Boolean) {
    if (shouldRestart) {
      startAnimation()
    }
    else {
      stopAnimation()
    }
  }
  
  fun startAnimation() {
    if (animator.isStarted || animator.isRunning) {
      animator.cancel()
      animator.currentPlayTime = 0
    }
    
    animator.start()
  }
  
  fun stopAnimation() {
    if (animator.isStarted || animator.isRunning) {
      animator.end()
    }
  }
  
  override fun onAnimationUpdate(animation: ValueAnimator?) {
    animation?.let {
      val params = layoutParams
      params.width = it.animatedValue as Int
      layoutParams = params
    }
  }
}