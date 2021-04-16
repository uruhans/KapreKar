package com.coloplast.olympus.bluetooth.overrides

import com.coloplast.olympus.ui.feature.UpdatedStateChange

interface Override {
  fun handleOverride(updatedStateChange: UpdatedStateChange) : UpdatedStateChange
}