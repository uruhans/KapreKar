package com.coloplast.olympus.bluetooth.businesslayer.enums

enum class BluetoothState {
  
  /**
   * Bluetooth Adapter is not available on the given OS. Most functions will throw [UnsupportedOperationException] when called.
   */
  BLUETOOTH_NOT_AVAILABLE,
  
  /**
   * Location permission is not given. Scanning and connecting to a device will not work. Used on API >=23.
   */
  LOCATION_PERMISSION_NOT_GRANTED,
  
  /**
   * Bluetooth Adapter is not switched on. Scanning and connecting to a device will not work.
   */
  BLUETOOTH_NOT_ENABLED,
  
  /**
   * Location Services are switched off. Scanning will not work. Used on API >=23.
   */
  LOCATION_SERVICES_NOT_ENABLED,
  
  /**
   * Everything is ready to be used.
   */
  READY,
  
  /**
   * State to define that we have not yet figured out what state we are in yet
   */
  UNDEFINED
}