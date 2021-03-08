package com.coloplast.olympus.shared.preferences

class MockPreferences : Preferences {
  override var activeTransmitter: String = ""
  override var latestStateFromActiveTx = ""
  override var latestDetach: String = ""
}