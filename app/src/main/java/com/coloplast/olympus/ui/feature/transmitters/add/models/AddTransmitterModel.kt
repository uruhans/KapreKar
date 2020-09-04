package com.coloplast.olympus.ui.feature.transmitters.add.models

import com.coloplast.olympus.ui.common.models.flow.FlowFactoryModel

class AddTransmitterModel :
  FlowFactoryModel {
  lateinit var transmitterNameToSearchFor : String
  var bluetoothEnabled: Boolean = true
}