package com.coloplast.olympus.ui.feature.transmitters.models

import com.coloplast.olympus.shared.models.DiffCallback

class SmallHeaderItem(val textResId: Int): DiffCallback {
  override fun areItemsTheSame(other: DiffCallback): Boolean = areContentsTheSame(other)

  override fun areContentsTheSame(other: DiffCallback): Boolean {
    return (other as? SmallHeaderItem)?.let {
      this.textResId == other.textResId
    } ?: false
  }
}