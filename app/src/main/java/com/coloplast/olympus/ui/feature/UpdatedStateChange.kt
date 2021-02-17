package com.coloplast.olympus.ui.feature

import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.patchsplit.models.ApsAction

class UpdatedStateChange(val stateChange: StateChange, val activeTransmitterChanged: Boolean = false, val wasPreviousStateMultipleTransmitters: Boolean = false, var action: ApsAction = ApsAction.Nothing())