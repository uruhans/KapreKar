package com.coloplast.olympus.ui.feature.home.models

/**
 * An interface determining whether the patch icon should be displayed on the main screen or not
 */
interface PatchVisible {
  
  /**
   * In some UiStates the patch is broken (ie: cutguard) - override this to set the state of the patch in error
   */
  fun patchInError() : Boolean = false
}