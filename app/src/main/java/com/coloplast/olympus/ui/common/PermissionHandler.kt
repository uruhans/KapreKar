package com.coloplast.olympus.ui.common

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bluelinelabs.conductor.Controller

/**
 * You should implement this interface in your Controller if you need to request a permission
 *
 * Pass the requested permission to the requestPermission method
 * Implement onPermissionGranted() and onPermissionDenied() to get the result from the user
 * Implement showPermissionRationale() to display a message to the user telling them why you need the
 * permission
 */
interface PermissionHandler {
  
  /**
   * request a specific permission - will also check to see if a rationale is to be shown
   */
  fun requestPermission(controller: Controller, requestCode: Int, permission: String) {
    requestPermission(controller, requestCode, permission, false)
  }
  
  /**
   * request specific permission again - this will skip rationale and request directly
   */
  fun requestPermissionAgain(controller: Controller, requestCode: Int, permission: String) {
    requestPermission(controller, requestCode, permission, true)
  }
  
  private fun requestPermission(
    controller: Controller, requestCode: Int, permission: String, skipRationale: Boolean
  ) {
    // if this permission already been granted, we skip
    if (ContextCompat.checkSelfPermission(
          controller.activity!!, permission
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      // just notify that the permission is granted
      onPermissionGranted(requestCode)
      return
    }
    
    // check to see if we need to display a rationale to the user
    if (!skipRationale && shouldShowRequestPermissionRationale(controller.activity!!, permission)) {
      showPermissionRationale(requestCode)
    }
    else {
      // request permission from the user
      controller.requestPermissions(arrayOf(permission), requestCode)
    }
  }
  
  fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(
        activity, permission
    )
  }
  
  /**
   * the users permission result is passed here and is either granted or denied
   */
  fun onPermissionsResult(
    requestCode: Int, permissions: Array<out String>, grantResults: IntArray
  ) {
    when (grantResults.first()) {
      PackageManager.PERMISSION_GRANTED -> onPermissionGranted(requestCode)
      PackageManager.PERMISSION_DENIED -> onPermissionDenied(requestCode)
    }
  }
  
  /**
   * implement this to tell the user why you are interested in getting the requested permission
   */
  fun showPermissionRationale(requestCode: Int)
  
  /**
   * User has granted permission to the requested permission, continue your work
   */
  fun onPermissionGranted(requestCode: Int)
  
  /**
   * User has denied permission to the requested permission. Disable the functionality and
   * let the user know that stuff wont work
   */
  fun onPermissionDenied(requestCode: Int)
}