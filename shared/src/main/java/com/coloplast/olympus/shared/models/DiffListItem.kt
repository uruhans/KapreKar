package com.coloplast.olympus.shared.models


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 26/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
interface DiffListItem {
  fun areItemsTheSame(other: DiffListItem): Boolean

  fun areContentsTheSame(other: DiffListItem): Boolean
}