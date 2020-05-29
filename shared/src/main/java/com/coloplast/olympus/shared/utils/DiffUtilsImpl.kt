package com.coloplast.olympus.shared.utils

import androidx.recyclerview.widget.DiffUtil
import com.coloplast.olympus.shared.models.DiffCallback


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 29/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class DiffUtilsImpl: DiffUtil.ItemCallback<DiffCallback>() {
  override fun areItemsTheSame(oldItem: DiffCallback, newItem: DiffCallback): Boolean =
    oldItem.areItemsTheSame(newItem)

  override fun areContentsTheSame(oldItem: DiffCallback, newItem: DiffCallback): Boolean =
    oldItem.areContentsTheSame(newItem)
}