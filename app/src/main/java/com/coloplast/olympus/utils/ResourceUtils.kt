package com.coloplast.olympus.utils

import com.coloplast.olympus.shared.AppSettings
import timber.log.Timber

object ResourceUtils {
  
  /**
   * gets the identifier based of the resourceName
   *
   * @return resourceId or 0 if no resource was found
   */
  fun getIdentifier(resourceName: String, defType: DefType): Int {
    val applicationContext = AppSettings.applicationContext
    val resourceId = applicationContext.resources.getIdentifier(
      resourceName.toLowerCase(),
      defType.type,
      applicationContext.packageName
    )
    
    if (resourceId == 0) {
      Timber.e("Could not find identifier for: ${resourceName.toLowerCase()}")
    }
    
    return resourceId
  }
}