package com.coloplast.olympus.ui.dialogs

import android.os.Bundle
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.bluelinelabs.conductor.Controller
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.databinding.ControllerDialogPatchBinding
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.models.Navigation
import com.coloplast.olympus.ui.feature.home.HomeController
import timber.log.Timber

class PatchDialogController(args: Bundle) : BaseDialogController<ControllerDialogPatchBinding>(args) {
  
  private val dismissAllowed: Boolean
  private val requestCode: Int
  
  val currentPatch = ObservableField<Patch>()
  val rawRotationalAngle = ObservableInt()
  
  init {
    dismissAllowed = args.getBoolean(BUNDLE_DISMISS_ALLOWED)
    requestCode = args.getInt(BUNDLE_REQUEST_CODE)
  }
  
  override fun attachVariables(viewBinding: ControllerDialogPatchBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerDialogPatchBinding) {
    // no-op
  
    val controller = Navigation.getContentRouter()!!.getControllerWithInstanceId(
      args.getString(BUNDLE_TARGET_INSTANCE_ID)!!
    )
    
    val homeController = controller as HomeController
    homeController.currentPatch.addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
      override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        currentPatch.set(homeController.currentPatch.get())
        rawRotationalAngle.set(homeController.rawRotationAngle.get())
      }
    })
  }
  
  fun onCloseClicked() {
    onDismissClicked()
  }
  
  fun onDismissClicked() {
    if (dismissAllowed) {
      super.dismissDialog(false)
    }
  }
  
  override fun onDismiss() {
    getListener(Listener::class.java)?.onDialogDismissed(requestCode)
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_dialog_patch
  }
  
  companion object {
    private const val BUNDLE_REQUEST_CODE = "bundle_request_code"
    private const val BUNDLE_DISMISS_ALLOWED = "bundle_dismiss_allowed"
    
    fun create(
      targetController: Controller,
      requestCode: Int,
      dismissAllowed: Boolean = false
    ): PatchDialogController {
      val args = Bundle().apply {
        putString(BUNDLE_TARGET_INSTANCE_ID, targetController.instanceId)
        putInt(BUNDLE_REQUEST_CODE, requestCode)
        putBoolean(BUNDLE_DISMISS_ALLOWED, dismissAllowed)
      }
      
      if (targetController !is Listener) {
        Timber.e("TargetController [$targetController] does not implement BaseDialogController.Listener")
      }
      
      return PatchDialogController(args)
    }
  }
}