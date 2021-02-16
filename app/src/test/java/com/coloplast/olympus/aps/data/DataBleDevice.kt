package com.coloplast.olympus.aps.data

import com.coloplast.olympus.bluetooth.businesslayer.enums.*
import com.coloplast.olympus.bluetooth.businesslayer.models.*
import com.coloplast.olympus.bluetooth.businesslayer.models.BleDevice
import com.coloplast.olympus.bluetooth.businesslayer.services.OlympusWirelessProtocol
import io.reactivex.Observable

object DataBleDevice {
  fun bleDevice(macAddress: String, connectionState: BleConnectionState = BleConnectionState.CONNECTED, statusVectorBytes: ByteArray) : BleDevice {
    return Observable.just(statusVectorBytes)
      .compose(OlympusWirelessProtocol.statusTransformer)
      .map { Peripheral(it.patch, it.transmitter, PowerLevel.HIGH) }
      .map { BleDevice("Mock", macAddress, connectionState, it) }
      .blockingFirst()
  }
}