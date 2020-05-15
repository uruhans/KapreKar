package com.coloplast.olympus.ui.feature.transmitters.add.at7

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


class AddTransmitterAT7Controller(args: Bundle = Bundle()) : MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }){

    override val resourceId: Int = R.raw.at7_plug_in_charger
    override val titleId: Int = R.string.guide_at7_title
    override val descriptionId: Int = R.string.guide_at7_body
    override val position: Int = 4
    override val totalCount: Int = 5

    companion object{
        private const val TAG_CONTROLLER = "add_transmitter_at7_controller"

        fun routerTransaction() = RouterTransaction.with(AddTransmitterAT7Controller())
            .tag(TAG_CONTROLLER)
    }
}