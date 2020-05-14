package com.coloplast.olympus.ui.common

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerImageGuideBinding
import com.coloplast.olympus.ui.feature.transmitters.add.at3.AddTransmitterAT3Controller


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 07/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
abstract class ImageGuideController(args: Bundle) : CustomToolbarController<ControllerImageGuideBinding>(args) {

    private val screenName: String = args.getString(SCREEN_NAME)!!

    abstract val imageId: Int
    abstract val titleId: Int
    abstract val descriptionId: Int
    abstract val position: Int
    abstract val totalCount: Int

    interface Listener{
        fun onNextClicked(screenName: String)
        fun onQuestionClicked(screenName: String)
    }

    override fun attachVariables(viewBinding: ControllerImageGuideBinding) {
        super.attachVariables(viewBinding)
        viewBinding.controller = this
    }

    fun onNextClicked() = getListener(Listener::class.java)!!.onNextClicked(screenName)

    fun onQuestionClicked() = getListener(Listener::class.java)!!.onQuestionClicked(screenName)

    override fun getLayout() = R.layout.controller_image_guide

    companion object{
        const val SCREEN_NAME = "SCREEN_NAME"
    }
}