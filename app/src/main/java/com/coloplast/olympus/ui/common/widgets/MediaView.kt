package com.coloplast.olympus.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.coloplast.olympus.databinding.ViewMediaBinding
import com.coloplast.olympus.utils.ResourceUtils
import java.lang.Exception


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 14/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 *
 * Custom view to be used in guides screens.
 * This will allow to use a resource, either an Image or a Video.
 */
class MediaView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val resId = ObservableInt()
    val isVideo = ObservableBoolean(true)

    private val inflater = LayoutInflater.from(context)
    private val view = ViewMediaBinding.inflate(inflater, this, true).apply {
        view = this@MediaView
    }

    fun setResource(resourceId: Int){
        isVideo.set(!ResourceUtils.isDrawableResource(context, resourceId))
        resId.set(resourceId)
    }
}