package com.coloplast.olympus.ui.common.widgets

import android.content.Context
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.annotation.RawRes
import androidx.databinding.ObservableBoolean
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ViewVideoPlayerBinding
import kotlinx.android.synthetic.main.view_video_player.view.*


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 12/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class VideoPlayer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val showPlay = ObservableBoolean(true)

    private val inflater = LayoutInflater.from(context)
    private val videoView = with(ViewVideoPlayerBinding.inflate(inflater, this, true)){
        this.view = this@VideoPlayer
        this.videoView
    }

    fun setVideoSrc(@RawRes videoId: Int){
        val videoPath = "android.resource://${context.packageName}/$videoId"
        val uri = Uri.parse(videoPath)

        videoView.setVideoURI(uri)
        showPreview()

        videoView.setOnCompletionListener { showPlay.set(true) }
    }

    fun playVideo(){
        showPlay.set(false)
        videoView.start()
    }

    // The image shown on the view before playing the video is just a black screen.
    // This forces an image by playing 100ms.
    private fun showPreview(){
        videoView.start()
        postDelayed({
            videoView.pause()
        }, 100)
    }
}