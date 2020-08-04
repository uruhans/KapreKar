package com.coloplast.olympus.ui.feature.guides.types

import androidx.annotation.StringRes
import com.coloplast.olympus.R
import com.coloplast.olympus.shared.types.TypeWithId


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 20/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
enum class GuidesMenuSubsection(
  override val id: Int,
  @StringRes val titleId: Int,
  @StringRes val descriptionId: Int,
  @StringRes val listTitleId: Int
): TypeWithId {
  GETTING_STARTED(0, R.string.guides_gs_title, R.string.guides_gs_description, R.string.guides_gs_list_title);
}