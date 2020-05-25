package com.coloplast.olympus.ui.feature

import com.coloplast.olympus.bluetooth.model.StateChange

class UpdatedStateChange(val activeTransmitterChanged: Boolean, val stateChange: StateChange, val oldState: StateChange? = null)