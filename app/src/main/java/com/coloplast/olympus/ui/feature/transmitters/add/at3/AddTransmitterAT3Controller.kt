package com.coloplast.olympus.ui.feature.transmitters.add.at3

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.ImageGuideController


class AddTransmitterAT3Controller : ImageGuideController(){

    override val imageId: Int = R.drawable.image_at3
    override val titleId: Int = R.string.guide_at3_title
    override val descriptionId: Int = R.string.guide_at3_body
    override val position: Int = 2

    companion object{
        private const val TAG_CONTROLLER = "add_transmitter_at3_controller"

        fun routerTransaction() = RouterTransaction.with(AddTransmitterAT3Controller())
            .tag(TAG_CONTROLLER)
    }


}