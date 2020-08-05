package com.coloplast.olympus.ui.common.widgets

import com.coloplast.olympus.shared.extensions.isValidEmail


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 14/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
interface EmailValidator: InputValidator {

  override fun validate(input: String): Boolean = input.isValidEmail()
}