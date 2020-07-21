package com.coloplast.olympus.ui.feature.guides.models

import androidx.annotation.StringRes


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 21/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
data class AccordionItem(
  @StringRes val questionId: Int,
  @StringRes val answerId: Int,
  val showContent: Boolean = false
)