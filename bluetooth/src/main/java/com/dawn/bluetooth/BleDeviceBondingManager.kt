package com.dawn.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import java.nio.charset.Charset

object BleDeviceBondingManager {
  
  fun createBond(context: Context, rxBleDevice: RxBleDevice): Observable<RxBleDevice> {
    return Observable.create { emitter ->
      when (rxBleDevice.bluetoothDevice.bondState) {
        BluetoothDevice.BOND_BONDED -> {
          emitter.onNext(rxBleDevice)
        }
        else -> {
          
          val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
              val deviceBeingPaired = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
              
              if (deviceBeingPaired?.address == rxBleDevice.bluetoothDevice.address) {
                val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)
                val prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                
                when {
                  state == BluetoothDevice.BOND_BONDED -> {
                    context.unregisterReceiver(this)
                    emitter.onNext(rxBleDevice)
                  }
                  state == BluetoothDevice.BOND_BONDING -> {
                  
                  }
                  state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDING -> {
                    emitter.onError(BleBondingFailedException())
                  }
                }
              }
            }
          }
          
          context.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
          rxBleDevice.bluetoothDevice.setPin("000000".toByteArray(Charset.forName("UTF-8")))

          val createBondResult = rxBleDevice.bluetoothDevice.createBond()
          if (!createBondResult) {
            emitter.onError(BleBondingFailedException())
          }
        }
      }
    }
  }
}