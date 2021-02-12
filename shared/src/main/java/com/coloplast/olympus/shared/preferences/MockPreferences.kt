package com.coloplast.olympus.shared.preferences

class MockPreferences : IPreferences {
  override var activeTransmitter: String = ""
  override var latestStateFromActiveTx = ""
}