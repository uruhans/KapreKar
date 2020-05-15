package com.coloplast.olympus.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.VideoView


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 12/05/2020.
 * Solution from: https://stackoverflow.com/a/4625578/2145504
 */
class ScalableVideoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : VideoView(context, attrs, defStyleAttr) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.getDefaultSize(0, widthMeasureSpec)
        val height = View.getDefaultSize(0, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }
}