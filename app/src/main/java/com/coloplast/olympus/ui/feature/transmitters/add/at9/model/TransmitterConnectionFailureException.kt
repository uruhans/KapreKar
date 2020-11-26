package com.coloplast.olympus.ui.feature.transmitters.add.at9.model

import java.lang.Exception

class TransmitterConnectionFailureException(val macAddress: String) : Exception()