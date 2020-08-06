package com.coloplast.olympus.ui.feature.guides.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.bluelinelabs.conductor.RouterTransaction


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 20/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
data class MenuItem(@DrawableRes val iconId: Int, @StringRes val stringId: Int, val destination: RouterTransaction)