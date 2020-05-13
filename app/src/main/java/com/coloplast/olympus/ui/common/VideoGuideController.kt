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
class VideoGuideController : CustomToolbarController<ControllerVideoGuideBinding>() {


    val videoId: Int = R.raw.at5_insert_charger_cable
     val titleId: Int = R.string.guide_at4_title
     val descriptionId: Int = R.string.guide_at4_body
     val position: Int = 1

   /* interface Listener{
        fun onNextClicked(screen: Screen)
        fun onQuestionClicked(screen: Screen)
    }
*/
    override fun attachVariables(viewBinding: ControllerVideoGuideBinding) {
        super.attachVariables(viewBinding)
       viewBinding.controller = this
    }
/*
    fun onNextClicked() = getListener(Listener::class.java)!!.onNextClicked(screen)

    fun onQuestionClicked() = getListener(Listener::class.java)!!.onQuestionClicked(screen)*/

    override fun getLayout() = R.layout.controller_video_guide

    companion object{
        private const val TAG_CONTROLLER = "add_transmitter_at4_controller"

        fun routerTransaction(): RouterTransaction {
            return RouterTransaction.with(VideoGuideController())
                .tag(TAG_CONTROLLER)
        }
    }
}