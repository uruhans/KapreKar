package com.coloplast.olympus.ui.feature.update.progress.model

import com.coloplast.olympus.utils.DeviceUtils

class UpdateModel(val macAddress: String, val currentAppVersion: String = DeviceUtils.versionName())