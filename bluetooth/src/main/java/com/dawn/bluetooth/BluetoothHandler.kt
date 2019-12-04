package com.dawn.bluetooth

import android.content.Context
import com.polidea.rxandroidble2.LogConstants
import com.polidea.rxandroidble2.LogOptions
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.Observable

class BluetoothHandler(context: Context, isDebugging: Boolean) {
  
  private val rxBleClient = RxBleClient.create(context)
  
  init {
    if (isDebugging) {
      val logOptions = LogOptions.Builder()
          .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
          .build()
      
      RxBleClient.updateLogOptions(logOptions)
    }
  }

  fun bluetoothStateObservable(): Observable<RxBleClient.State> {
    return rxBleClient.observeStateChanges()
        .startWith(rxBleClient.state)
  }
  
  fun scanBleDevices(): Observable<ScanResult> {
    val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .build()
    
    return rxBleClient.scanBleDevices(scanSettings)
  }
  
  fun connectBleDevice(macAddress: String) {
  
  }
}