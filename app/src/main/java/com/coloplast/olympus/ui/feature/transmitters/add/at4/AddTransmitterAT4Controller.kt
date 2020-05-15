package com.coloplast.olympus.ui.feature.transmitters.add.at4

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAddTransmitterAt4Binding
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.widgets.InputCodeEditText
import com.coloplast.olympus.ui.feature.transmitters.add.at10.AddTransmitterAT10Controller


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 11/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class AddTransmitterAT4Controller: CustomToolbarController<ControllerAddTransmitterAt4Binding>(),
    InputCodeEditText.Listener {

    interface Listener{
        fun onCodeInput(code: String)
    }

    override fun attachVariables(viewBinding: ControllerAddTransmitterAt4Binding) {
        super.attachVariables(viewBinding)
        viewBinding.controller = this
    }

    override fun onCodeInput(code: String) {
        getListener(Listener::class.java)!!.onCodeInput(code)
    }

    override fun getLayout(): Int = R.layout.controller_add_transmitter_at_4

    companion object {
        private const val TAG_CONTROLLER = "add_transmitter_at4_controller"

        fun routerTransaction(): RouterTransaction {
            return RouterTransaction.with(AddTransmitterAT4Controller())
                    .tag(TAG_CONTROLLER)
        }
    }
}