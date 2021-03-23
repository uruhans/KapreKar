package com.coloplast.olympus.bluetooth.businesslayer.models

enum class TransmitterState {
  OK,
  PATCH_BROKEN,
  WATER_INGRESS_DETECTION,
  DETACHED,
  BATTERY_CHARGING,
  BATTERY_SHUTTING_DOWN,
  UNKNOWN,
}