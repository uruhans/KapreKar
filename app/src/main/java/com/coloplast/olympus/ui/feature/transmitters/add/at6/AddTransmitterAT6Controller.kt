package com.coloplast.olympus.ui.feature.transmitters.add.at6

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


class AddTransmitterAT6Controller(args: Bundle = Bundle()) : MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }){

    override val resourceId: Int = R.raw.at6_connect_to_power
    override val titleId: Int = R.string.guide_at6_title
    override val descriptionId: Int = R.string.guide_at6_body
    override val position: Int = 3
    override val totalCount: Int = 5

    companion object{
        private const val TAG_CONTROLLER = "add_transmitter_at6_controller"

        fun routerTransaction() = RouterTransaction.with(AddTransmitterAT6Controller())
            .tag(TAG_CONTROLLER)
    }
}