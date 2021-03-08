package com.coloplast.olympus.ui.feature

import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.patchsplit.models.ApsMethod

class UpdatedStateChange(val stateChange: StateChange, val activeTransmitterChanged: Boolean = false, var apsMethod: ApsMethod? = null)