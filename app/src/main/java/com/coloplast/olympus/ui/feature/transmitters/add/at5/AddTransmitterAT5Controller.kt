package com.coloplast.olympus.ui.feature.transmitters.add.at5

import android.os.Bundle
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener


class AddTransmitterAT5Controller(args: Bundle = Bundle()) : MediaGuideController(args.apply { putString(SCREEN_NAME, TAG_CONTROLLER) }) , TopMenuVisibilityListener{

    override val resourceId: Int = R.raw.at5_insert_charger_cable
    override val titleId: Int = R.string.guide_at5_title
    override val descriptionId: Int = R.string.guide_at5_body
    override val position: Int = 2
    override val totalCount: Int = 5

    override fun showCloseButton(): Boolean = false

    companion object{
        private const val TAG_CONTROLLER = "add_transmitter_at5_controller"

        fun routerTransaction() = RouterTransaction.with(AddTransmitterAT5Controller())
            .tag(TAG_CONTROLLER)
    }
}