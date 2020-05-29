package com.coloplast.olympus.ui.feature

import com.coloplast.olympus.bluetooth.model.StateChange

class UpdatedStateChange(val stateChange: StateChange, val activeTransmitterChanged: Boolean = false)