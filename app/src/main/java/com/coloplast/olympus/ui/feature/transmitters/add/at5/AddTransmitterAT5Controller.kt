package com.coloplast.olympus.ui.feature.transmitters.add.at5

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController


class AddTransmitterAT5Controller(args: Bundle = Bundle()) : MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }){

    override val resourceId: Int = R.raw.at5_insert_charger_cable
    override val titleId: Int = R.string.guide_at4_title
    override val descriptionId: Int = R.string.guide_at4_body
    override val position: Int = 2
    override val totalCount: Int = 5

    companion object{
        private const val TAG_CONTROLLER = "add_transmitter_at5_controller"

        fun routerTransaction() = RouterTransaction.with(AddTransmitterAT5Controller())
            .tag(TAG_CONTROLLER)
    }
}