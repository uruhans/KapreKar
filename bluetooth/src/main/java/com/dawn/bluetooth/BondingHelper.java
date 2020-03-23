package com.dawn.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.polidea.rxandroidble2.RxBleDevice;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposables;

/**
 * In the communication with the scale device, some characteristics are encrypted. Therefore, Android
 * needs to be "Bonded" with the device. In order to be bond, usually what you need is a passkey. However,
 * in this case the Bonding mechanism is a "JustWorks" bonding, in which no passkey is required. The JustWorks
 * bonding happens automatically the first time the phone interacts with the device.
 * Depending on the bluetooth device and the android phone, an error can happen if the device had been
 * bonded in a previous connection. In that case, the first time trying to read or write on an
 * encrypted characteristic, the operation will fail after timeuot (30s by default) with a GATT_INSUF_AUTH
 * error. This will cause the device to unbond and bond again. After reseting the bonding, the ongoing
 * operations will work succesfuly. Chaining a retry() after the first read/write operation would be
 * a possible workaround, but it won't get to the retry() until the timeout has been triggered.
 * <p>
 * To prevent that situation, I created this helper class that will do the following:
 * 1- Check if the devices is bonded. If it is not bonded, it will start the bonding process (step #4)
 * 2- If it is already bonded, it will create a BroadcastReceiver, start the unbonding operation and
 * continue once the Receiver gets a new STATE equal to BOND_NONE.
 * 3- Once it is onbonded, the Disposable will unregister the receiver.
 * 4- Afterwards, it will try to bond with the device. For that, it creates a BroadcastReceiver, start
 * the bonding operation and wait until the Receiver gets a new STATE equal to BOND_BONDED.
 * 5- The disposable will unregister the receiver.
 * 6- It will retry everything once if an error occur.
 * 7- The whole process, including the retry(), can have a time limit passed as a parameter.
 */
public class BondingHelper {

  private static int DEFAULT_TIMEOUT = 30;

  public static class BondingFailedException extends RuntimeException {
  }

  public static Completable bondWithDevice(final Context context, final RxBleDevice rxBleDevice) {
    return bondWithDevice(context, rxBleDevice, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
  }

  public static Completable bondWithDevice(final Context context, final RxBleDevice rxBleDevice,
                                           long timeout, TimeUnit timeunit) {
    return removeBond(context, rxBleDevice.getBluetoothDevice())
        .andThen(Completable.create(completion -> {
          Log.d("BondingHelper", "Creating Bonding Broadcast Receiver.");
          final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
              final BluetoothDevice deviceBeingPaired = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

              if (deviceBeingPaired != null && intent.getAction() != null && intent.getAction().equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);

                Log.d("BondingHelper", "PairingVariant: " + type);
                Log.d("BondingHelper", "BluetoothDevice.ACTION_PAIRING_REQUEST - setting pin");

                deviceBeingPaired.setPin(convertPinToBytes("123456"));
                deviceBeingPaired.createBond();


                //abortBroadcast();


//                                try {
//                                    Method m = deviceBeingPaired.getClass().getMethod("createBond", int.class);
//                                    m.invoke(deviceBeingPaired, BluetoothDevice.TRANSPORT_LE);
//
//                                    deviceBeingPaired.setPin(convertPinToBytes("123456"));
//                                } catch (Exception e) {
//                                    Log.d("BondingHelper", "reflection failed");
//                                    //completion.tryOnError(new BondingFailedException());
//                                }

              } else {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
                Log.d("BondingHelper", String.format("Intent received in Bonding Broadcast Receiver. State %1$s - Device %2$s", state, deviceBeingPaired.getAddress()));
                if (deviceBeingPaired.getAddress().equals(rxBleDevice.getMacAddress())) {
                  if (state == BluetoothDevice.BOND_BONDED) {
                    Log.d("BondingHelper", "State is BOND_BONDED. Bonded Succeded.");
                    completion.onComplete();
                  } else if (state == BluetoothDevice.BOND_NONE) {
                    Log.d("BondingHelper", "State is BOND_NONE. Bonding Failed.");
                    completion.tryOnError(new BondingFailedException());
                  } else {
                    Log.d("BondingHelper", "State is something else. " + state);
                  }
                }
              }
            }
          };

          completion.setDisposable(Disposables.fromAction(() -> {
            Log.d("BondingHelper", "Disposing Bonding completable and unregistering Broadcast Receiver");
            context.unregisterReceiver(receiver);
          }));

          IntentFilter filter = new IntentFilter();
          filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
          filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
          filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);

          context.registerReceiver(receiver, filter);

                    Log.d("BondingHelper", "Call for creating bond.");
                    //This returns false in immediate failure or true if the bonding can begin
                    final boolean createBondResult = rxBleDevice.getBluetoothDevice().createBond();

                    if (!createBondResult) {
                        Log.d("BondingHelper", "Could not start bonding process.");
                        completion.tryOnError(new BondingFailedException());
                    }
        }))
        .retry() //I give the bonding process one more chance.
        .timeout(timeout, timeunit)
        .doOnError(throwable -> {
          Log.e("BondingHelper", String.format("Timeout of %1$i %2$s during bonding process.",
              timeout, timeunit.toString()));
          throw new BondingFailedException();
        });
  }

  private static Completable removeBond(final Context context, BluetoothDevice device) {
    return Completable.create(completion -> {
      // If it was already bonded. I delete the bonding because it needs to be re-bonded.

      Log.d("BondingHelper", "Creating Unbonding Broadcast Receiver.");
      final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context c, final Intent intent) {
          final BluetoothDevice deviceBeingUnpaired = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
          final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);

          Log.d("BondingHelper", String.format("Intent received in Unbonding Broadcast Receiver. State %1$s - Device %2$s",
              state, deviceBeingUnpaired.getAddress()));
          if (deviceBeingUnpaired.getAddress().equals(device.getAddress())) {
            if (state == BluetoothDevice.BOND_NONE) {
              Log.d("BondingHelper", "State received is BOND_NONE. Unbonding succeded.");
              //context.unregisterReceiver(this);
              completion.onComplete();
            } else if (state == BluetoothDevice.BOND_BONDED) {
              Log.d("BondingHelper", "State received is BOND_BONDED. Unbonding failed.");
              //context.unregisterReceiver(this);
              //completion.tryOnError(new BondingFailedException());
            } else {
              Log.d("BondingHelper", "State is something else." + state);
            }
          }
        }
      };

      completion.setDisposable(Disposables.fromAction(() -> {
        Log.d("BondingHelper", "Disposing Unbonding completable and unregistering Broadcast Receiver");
        context.unregisterReceiver(receiver);
      }));
      context.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

      Log.d("BondingHelper", "Checking bond status.");
      if (device.getBondState() != BluetoothDevice.BOND_NONE) {
        Log.d("BondingHelper", "The device was already bonded. Removing bond.");
        try {
          Method m = device.getClass().getMethod("removeBond", (Class[]) null);
          m.invoke(device, (Object[]) null);
        } catch (Exception e) {
          completion.tryOnError(new BondingFailedException());
        }
      } else {
        Log.d("BondingHelper", "The device was not bond.");
        completion.onComplete();
      }
    });
  }

  public static byte[] convertPinToBytes(String pin) {
    if (pin == null) {
      return null;
    }

    byte[] pinBytes = pin.getBytes(StandardCharsets.UTF_8);
    if (pinBytes.length <= 0 || pinBytes.length > 16) {
      return null;
    }
    return pinBytes;
  }
}