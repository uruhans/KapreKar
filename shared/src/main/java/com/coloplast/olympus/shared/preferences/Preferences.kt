package com.coloplast.olympus.shared.preferences

/**
 * This interface was made in order to properly test the APS system,
 * and these are the only two fields needed for those tests
 */
interface Preferences {
  var activeTransmitter: String
  var latestStateFromActiveTx: String
  var latestDetach: String
}