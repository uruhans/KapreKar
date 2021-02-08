package com.coloplast.olympus.patchsplit

import com.coloplast.olympus.ui.common.extensions.toMillis
import java.time.LocalDateTime

class CompromisedPackage(val locked: Int, val running: Int, val timestamp: Long = LocalDateTime.now().toMillis())