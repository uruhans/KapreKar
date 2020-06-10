package com.coloplast.olympus.ui.feature.transmitters.models

import com.coloplast.olympus.shared.models.DiffListItem

class SmallHeaderItem(val textResId: Int): DiffListItem {
  override fun areItemsTheSame(other: DiffListItem): Boolean = areContentsTheSame(other)

  override fun areContentsTheSame(other: DiffListItem): Boolean {
    return (other as? SmallHeaderItem)?.let {
      this.textResId == other.textResId
    } ?: false
  }
}