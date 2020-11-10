package com.coloplast.olympus.work

import com.coloplast.olympus.shared.types.PackageType

class RetryException(val packageType: PackageType, val tuplesRemaining: Int) : Exception()