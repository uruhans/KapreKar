package com.coloplast.olympus.ui.feature.transmitters.models

import com.coloplast.olympus.shared.models.DiffListItem

class SectionDivider: DiffListItem {
  override fun areItemsTheSame(other: DiffListItem): Boolean = true
  override fun areContentsTheSame(other: DiffListItem): Boolean = true
}