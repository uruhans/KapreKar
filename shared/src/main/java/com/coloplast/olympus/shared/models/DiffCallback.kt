package com.coloplast.olympus.shared.models


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 26/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
interface DiffCallback {
  fun areItemsTheSame(other: DiffCallback): Boolean

  fun areContentsTheSame(other: DiffCallback): Boolean
}