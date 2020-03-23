package com.dawn.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable

class PairingFailedException : RuntimeException()

object PairingManager {
  
  /**
   * @throws PairingFailedException
   */
  // TODO: try to understand why the popup is being displayed in the notification center (sometimes) ???!!!
  fun pairWithDevice(context: Context, rxBleDevice: RxBleDevice): Observable<RxBleDevice> {
    return Observable.create { emitter ->
      when (rxBleDevice.bluetoothDevice.bondState) {
        BluetoothDevice.BOND_BONDED -> {
          Log.d("pairing", "we are already bonded - completing")
          emitter.onNext(rxBleDevice)
        }
        else -> {
          
          val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
              val deviceBeingPaired = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
              
              if (deviceBeingPaired?.address == rxBleDevice.bluetoothDevice.address) {
                val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)
                val prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                
                if (state == BluetoothDevice.BOND_BONDED) {
                  Log.d("pairing", "BOND_BONDED | $prevState")
                  context.unregisterReceiver(this)
                  Log.d("pairing", "we are bonded - completing")
                  emitter.onNext(rxBleDevice)
                }
                else if (state == BluetoothDevice.BOND_BONDING) {
                  Log.d("pairing", "BOND_BONDING | $prevState")
                }
                else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDING) {
                  Log.d("pairing", "BOND_NONE | $prevState")
                  emitter.onError(PairingFailedException())
                }
              }
            }
          }
          
          //emitter.setDisposable(Disposables.fromAction {
          //  Log.d("pairing", "unregister receiver")
          //  context.unregisterReceiver(receiver)
          //})
          context.registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
          
          //rxBleDevice.bluetoothDevice.setPin(byteArrayOf(1.toByte(), 2.toByte(), 3.toByte(), 4.toByte(), 5.toByte(), 6.toByte()))
          val createBondResult = rxBleDevice.bluetoothDevice.createBond()
          
          if (!createBondResult) {
            emitter.onError(PairingFailedException())
          }
        }
      }
    }
  }
}