package com.coloplast.olympus.ui.feature.transmitters.models

import com.coloplast.olympus.shared.models.DiffCallback

class SectionDivider: DiffCallback {
  override fun areItemsTheSame(other: DiffCallback): Boolean = true
  override fun areContentsTheSame(other: DiffCallback): Boolean = true
}