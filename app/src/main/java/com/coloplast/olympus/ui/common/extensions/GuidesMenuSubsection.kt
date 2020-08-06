package com.coloplast.olympus.ui.common.extensions

import androidx.annotation.StringRes
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.feature.guides.types.GuidesMenuSubsection


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 04/08/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */

@StringRes
fun GuidesMenuSubsection.getTitle(): Int = when(this){
  GuidesMenuSubsection.GETTING_STARTED -> R.string.guides_gs_title
  GuidesMenuSubsection.APPLYING_CHANGING -> R.string.guides_ac_title
  GuidesMenuSubsection.CONNECTION_AND_TRANSMITTER -> R.string.guides_ct_title
  GuidesMenuSubsection.MAINTENANCE -> R.string.guides_m_title
}


@StringRes
fun GuidesMenuSubsection.getDescription(): Int = when(this){
  GuidesMenuSubsection.GETTING_STARTED -> R.string.guides_gs_description
  GuidesMenuSubsection.APPLYING_CHANGING -> R.string.guides_ac_description
  GuidesMenuSubsection.CONNECTION_AND_TRANSMITTER -> R.string.guides_ct_description
  GuidesMenuSubsection.MAINTENANCE -> R.string.guides_m_description
}

@StringRes
fun GuidesMenuSubsection.getListTitle(): Int = when(this){
  GuidesMenuSubsection.GETTING_STARTED -> R.string.guides_gs_list_title
  GuidesMenuSubsection.APPLYING_CHANGING -> R.string.guides_ac_list_title
  GuidesMenuSubsection.CONNECTION_AND_TRANSMITTER -> R.string.guides_ct_list_title
  GuidesMenuSubsection.MAINTENANCE -> R.string.guides_m_list_title
}