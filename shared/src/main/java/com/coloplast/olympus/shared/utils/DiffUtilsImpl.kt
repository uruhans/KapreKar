package com.coloplast.olympus.shared.utils

import androidx.recyclerview.widget.DiffUtil
import com.coloplast.olympus.shared.models.DiffListItem


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 29/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class DiffUtilsImpl: DiffUtil.ItemCallback<DiffListItem>() {
  override fun areItemsTheSame(oldItem: DiffListItem, newItem: DiffListItem): Boolean =
    oldItem.areItemsTheSame(newItem)

  override fun areContentsTheSame(oldItem: DiffListItem, newItem: DiffListItem): Boolean =
    oldItem.areContentsTheSame(newItem)
}