package com.coloplast.olympus.ui.dialogs

import android.os.Bundle
import com.bluelinelabs.conductor.Controller
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerDialogCallHelpBinding
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import timber.log.Timber

class CallHelpDialogController(args: Bundle) : BaseDialogController<ControllerDialogCallHelpBinding>(args){

  interface Listener : BaseDialogController.Listener {
    fun onCallClicked(requestCode: Int, number: String)
  }

  val dismissAllowed = args.getBoolean(BUNDLE_DISMISS_ALLOWED)
  private val requestCode = args.getInt(BUNDLE_REQUEST_CODE)

  override fun attachVariables(viewBinding: ControllerDialogCallHelpBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerDialogCallHelpBinding) {
    /* no-op */
  }

  fun onDismissClicked() {
    super.dismissDialog(false)
    onDismiss()
  }

  fun onCloseClicked() {
    super.dismissDialog(false)
    onDismiss()
  }

  fun onCallClicked() {
    getListener(Listener::class.java)?.onCallClicked(requestCode, getString(R.string.dialog_call_help_phone_number).toString())
  }

  override fun onDismiss() {
    getListener(Listener::class.java)?.onDialogDismissed(requestCode)
  }

  override fun getLayout(): Int {
    return R.layout.controller_dialog_call_help
  }

  companion object {
    private const val BUNDLE_REQUEST_CODE = "bundle_request_code"
    private const val BUNDLE_DISMISS_ALLOWED = "bundle_dismiss_allowed"

    fun create(
      targetController: Controller,
      requestCode: Int,
      dismissAllowed: Boolean = true
    ): CallHelpDialogController {
      val args = Bundle().apply {
        putString(BUNDLE_TARGET_INSTANCE_ID, targetController.instanceId)
        putInt(BUNDLE_REQUEST_CODE, requestCode)
        putBoolean(BUNDLE_DISMISS_ALLOWED, dismissAllowed)
      }

      if (targetController !is Listener) {
        Timber.e("TargetController [$targetController] does not implement BaseDialogController.Listener")
      }

      return CallHelpDialogController(args)
    }
  }
}