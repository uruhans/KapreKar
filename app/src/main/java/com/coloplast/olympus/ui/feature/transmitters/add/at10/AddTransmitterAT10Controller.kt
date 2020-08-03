package com.coloplast.olympus.ui.feature.transmitters.add.at10

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAddTransmitterAt10Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class AddTransmitterAT10Controller: BaseController<ControllerAddTransmitterAt10Binding>(){

    override fun attachVariables(viewBinding: ControllerAddTransmitterAt10Binding) {
        viewBinding.controller = this
    }

    override fun onViewBound(viewBinding: ControllerAddTransmitterAt10Binding) {
        //noop
    }

    fun onCloseButtonClicked(){
        getListener(FlowListener::class.java)!!.onNextClicked()
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