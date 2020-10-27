package com.coloplast.olympus.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.RawRes
import androidx.databinding.ObservableBoolean
import com.coloplast.olympus.databinding.ViewVideoPlayerBinding
import com.coloplast.olympus.extensions.getResourceUri


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 12/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class VideoPlayer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val showPlay = ObservableBoolean(true)
    private val looping = ObservableBoolean(true)

    private val inflater = LayoutInflater.from(context)
    private val videoView = with(ViewVideoPlayerBinding.inflate(inflater, this, true)){
        this.view = this@VideoPlayer
        this.videoView
    }.also {
        it.alpha = 0f
        it.setOnPreparedListener { mp ->
            mp.isLooping = looping.get()
            playVideo()
        }
        it.setOnCompletionListener { showPlay.set(true) }
    }

    fun setVideoSrc(@RawRes videoId: Int){
        videoView.setVideoURI(context.getResourceUri(videoId))
    }

    fun setIsLooping(loop : Boolean) {
        looping.set(loop)
    }

    fun playVideo(){
        showPlay.set(false)
        videoView.start()
        videoView.animate().alpha(1F).setInterpolator(AccelerateInterpolator(2F)).setDuration(500).start() // we prevent a black screen by animating the view
    }

}