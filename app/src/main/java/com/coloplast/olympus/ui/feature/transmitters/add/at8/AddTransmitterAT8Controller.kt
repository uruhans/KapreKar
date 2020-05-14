package com.coloplast.olympus.ui.feature.transmitters.add.at8

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


class AddTransmitterAT8Controller(args: Bundle = Bundle()) : MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }){

    override val resourceId: Int = R.raw.at8_press_button_pairing_mode
    override val titleId: Int = R.string.guide_at8_title
    override val descriptionId: Int = R.string.guide_at8_body
    override val position: Int = 5
    override val totalCount: Int = 5

    companion object{
        private const val TAG_CONTROLLER = "add_transmitter_at8_controller"

        fun routerTransaction() = RouterTransaction.with(AddTransmitterAT8Controller())
            .tag(TAG_CONTROLLER)
    }
}