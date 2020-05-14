package com.coloplast.olympus.ui.common

import android.os.Bundle
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerMediaGuideBinding


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 07/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 *
 * Guide base screen. Allows either a @Drawable image or a @Raw video
 */
abstract class MediaGuideController(args: Bundle) : CustomToolbarController<ControllerMediaGuideBinding>(args) {

    private val screenName: String = args.getString(SCREEN_NAME)!!

    abstract val resourceId: Int
    abstract val titleId: Int
    abstract val descriptionId: Int
    abstract val position: Int
    abstract val totalCount: Int

    interface Listener{
        fun onNextClicked(screen: String)
        fun onQuestionClicked(screen: String)
    }
    override fun attachVariables(viewBinding: ControllerMediaGuideBinding) {
        super.attachVariables(viewBinding)
       viewBinding.controller = this
    }

    fun onNextClicked() = getListener(Listener::class.java)!!.onNextClicked(screenName)

    fun onQuestionClicked() = getListener(Listener::class.java)!!.onQuestionClicked(screenName)

    override fun getLayout() = R.layout.controller_media_guide

    companion object{
        const val SCREEN_NAME = "SCREEN_NAME"
    }
}