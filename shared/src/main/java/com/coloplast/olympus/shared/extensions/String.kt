package com.coloplast.olympus.shared.extensions

import android.util.Patterns

fun String.isValidEmail() = this.isNotEmpty() && this.matches(Patterns.EMAIL_ADDRESS.toRegex())