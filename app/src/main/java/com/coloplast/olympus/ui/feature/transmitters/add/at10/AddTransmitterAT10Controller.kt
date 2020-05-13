package com.coloplast.olympus.ui.feature.transmitters.add.at10

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAddTransmitterAt10Binding
import com.coloplast.olympus.ui.common.CustomToolbarController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class AddTransmitterAT10Controller: CustomToolbarController<ControllerAddTransmitterAt10Binding>(){

    interface Listener{
        fun closeAddTransmitterFlow()
    }

    override fun attachVariables(viewBinding: ControllerAddTransmitterAt10Binding) {
        super.attachVariables(viewBinding)
        viewBinding.controller = this
    }

    fun onCloseButtonClicked(){
        getListener(Listener::class.java)!!.closeAddTransmitterFlow()
    }

    override fun getLayout(): Int = R.layout.controller_add_transmitter_at_10

    companion object {
        private const val TAG_CONTROLLER = "add_transmitter_at10_controller"

        fun routerTransaction(): RouterTransaction {
            return RouterTransaction.with(AddTransmitterAT10Controller())
                    .tag(TAG_CONTROLLER)
        }
    }
}