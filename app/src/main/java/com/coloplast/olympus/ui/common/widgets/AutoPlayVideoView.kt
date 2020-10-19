package com.coloplast.olympus.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.VideoView
import androidx.annotation.RawRes
import com.coloplast.olympus.extensions.getResourceUri

/**
 * A custom view that plays a video automatically as soon as it is attached to the screen
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class AutoPlayVideoView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : VideoView(context, attrs, defStyleAttr) {


  fun setVideoSrc(@RawRes videoId: Int) {
    val uri = context.getResourceUri(videoId)
    setVideoURI(uri)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    start()
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    pause()
  }

  /* We want this view to stretch to the maximum width of the screen while keeping the aspect ratio
   * Otherwise, the view size will be the resolution of the video it displays
   * Solution from: https://stackoverflow.com/a/4625578/2145504
   */
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = View.getDefaultSize(0, widthMeasureSpec)
    val height = View.getDefaultSize(0, heightMeasureSpec)
    setMeasuredDimension(width, height)
  }
}