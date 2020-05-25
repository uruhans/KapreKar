package com.coloplast.olympus.ui.common.extensions

import androidx.databinding.ObservableBoolean


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 25/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */

fun ObservableBoolean.toggle() = set(!get())