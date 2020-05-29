package com.coloplast.olympus.ui.dialogs

import android.os.Bundle
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.bluelinelabs.conductor.Controller
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.databinding.ControllerDialogPatchBinding
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.models.Navigation
import com.coloplast.olympus.ui.feature.home.BreachesOccurred
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.utils.Strings
import org.koin.core.inject
import timber.log.Timber

class PatchDialogController(args: Bundle) : BaseDialogController<ControllerDialogPatchBinding>(args) {

  private val breachesOccurred: BreachesOccurred by inject()

  private val dismissAllowed: Boolean
  private val requestCode: Int
  
  val currentPatch = ObservableField<Patch>()
  val rawRotationalAngle = ObservableInt()

  val breachDetected = ObservableBoolean(false)

  val firstBreach = ObservableField<String>()
  val secondBreach = ObservableField<String>()

  private val controller = Navigation.getContentRouter()!!.getControllerWithInstanceId(
    args.getString(BUNDLE_TARGET_INSTANCE_ID)!!
  ) as? HomeController

  init {
    dismissAllowed = args.getBoolean(BUNDLE_DISMISS_ALLOWED)
    requestCode = args.getInt(BUNDLE_REQUEST_CODE)
  }
  
  override fun attachVariables(viewBinding: ControllerDialogPatchBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerDialogPatchBinding) {
    controller?.currentPatch?.addOnPropertyChangedCallback(currentPatchChangedCallback)
    setDataFromHomeController()
  }

  fun onCloseClicked() {
    super.dismissDialog(false)
  }
  
  fun onDismissClicked() {
    if (dismissAllowed) {
      super.dismissDialog(false)
    }
  }

  private fun setDataFromHomeController() {
    currentPatch.set(controller?.currentPatch?.get())
    rawRotationalAngle.set(controller?.rawRotationAngle?.get() ?: 0)

    val breaches = breachesOccurred.getBreaches()

    if(breaches.isNotEmpty()) {
      breachDetected.set(true)
      firstBreach.set("${getString(R.string.overlay_breach_first)} ${Strings.getTimeString(breaches[0].localDateTime)}")

      if(breaches.size > 1) {
        secondBreach.set("${getString(R.string.overlay_breach_second)} ${Strings.getTimeString(breaches[1].localDateTime)}")
      }
    }
  }

  private val currentPatchChangedCallback = object : Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
      setDataFromHomeController()
    }
  }
  
  override fun onDismiss() {
    controller?.currentPatch?.removeOnPropertyChangedCallback(currentPatchChangedCallback)
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