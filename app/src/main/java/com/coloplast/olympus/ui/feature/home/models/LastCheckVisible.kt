package com.coloplast.olympus.ui.feature.home.models

/**
 * An interface determining whether the last check should be displayed on the main screen or not
 */
interface LastCheckVisible {
  
  fun lastCheckVisible() : Boolean = true
}