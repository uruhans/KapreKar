package com.coloplast.olympus.ui.common

import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerImageGuideBinding
import com.coloplast.olympus.databinding.ControllerVideoGuideBinding
import com.coloplast.olympus.ui.feature.transmitters.add.at1.AddTransmitterAT1Controller
import com.coloplast.olympus.ui.feature.transmitters.add.at3.AddTransmitterAT3Controller


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 07/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
abstract class VideoGuideController() : CustomToolbarController<ControllerVideoGuideBinding>() {

    private val screenName: String = this.javaClass.simpleName

    abstract val videoId: Int
    abstract val titleId: Int
    abstract val descriptionId: Int
    abstract val position: Int
    abstract val totalCount: Int

    interface Listener{
        fun onNextClicked(screen: String)
        fun onQuestionClicked(screen: String)
    }
    override fun attachVariables(viewBinding: ControllerVideoGuideBinding) {
        super.attachVariables(viewBinding)
       viewBinding.controller = this
    }

    fun onNextClicked() = getListener(Listener::class.java)!!.onNextClicked("screen")

    fun onQuestionClicked() = getListener(Listener::class.java)!!.onQuestionClicked("adsada")

    override fun getLayout() = R.layout.controller_video_guide

    companion object{
        const val SCREEN_NAME = "SCREEN_NAME"
    }
}