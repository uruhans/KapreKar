package com.coloplast.olympus.ui.common.dialogs

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableInt
import com.bluelinelabs.conductor.Controller
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerDialogBinding
import timber.log.Timber

@Suppress("PrivatePropertyName")
class DialogController(args: Bundle?) : BaseDialogController<ControllerDialogBinding>(args) {
  
  val titleId = ObservableInt(R.string.generic_error)
  val titleFormatArgs = ObservableArrayList<String>()
  
  val messageId = ObservableInt(R.string.generic_error_message)
  val messageFormatArgs = ObservableArrayList<String>()
  
  val positiveButtonTextId = ObservableInt()
  val neutralButtonTextId = ObservableInt(0)
  val negativeButtonTextId = ObservableInt(0)
  
  private val dismissAllowed: Boolean
  private val requestCode: Int
  
  init {
    args!! // will cause a crash (on purpose)
    
    titleId.set(args.getInt(BUNDLE_TITLE_RES_ID))
    messageId.set(args.getInt(BUNDLE_MESSAGE_RES_ID))
    
    args.getStringArrayList(BUNDLE_MESSAGE_FORMAT_ARGS)
        ?.let {
          messageFormatArgs.addAll(it)
        }
    
    positiveButtonTextId.set(args.getInt(BUNDLE_POSITIVE_BUTTON_RES_ID))
    neutralButtonTextId.set(args.getInt(BUNDLE_NEUTRAL_BUTTON_RES_ID))
    negativeButtonTextId.set(args.getInt(BUNDLE_NEGATIVE_BUTTON_RES_ID))
    
    dismissAllowed = args.getBoolean(BUNDLE_DISMISS_ALLOWED)
    requestCode = args.getInt(BUNDLE_REQUEST_CODE)
  }
  
  override fun onViewBound(viewBinding: ControllerDialogBinding) {
    // no-op
  }
  
  override fun attachVariables(viewBinding: ControllerDialogBinding) {
    viewBinding.controller = this
  }
  
  fun onPositiveButtonClicked() {
    getListener(Listener::class.java)?.onPositiveButtonClicked(requestCode)
  }
  
  fun onNeutralButtonClicked() {
    getListener(Listener::class.java)?.onNeutralButtonClicked(requestCode)
  }
  
  fun onNegativeButtonClicked() {
    getListener(Listener::class.java)?.onNegativeButtonClicked(requestCode)
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
    return R.layout.controller_dialog
  }
  
  companion object {
    private const val BUNDLE_REQUEST_CODE = "bundle_request_code"
    private const val BUNDLE_TITLE_RES_ID = "bundle_title_res_id"
    private const val BUNDLE_TITLE_FORMAT_ARGS = "bundle_title_format_args"
    private const val BUNDLE_MESSAGE_RES_ID = "bundle_message_res_id"
    private const val BUNDLE_MESSAGE_FORMAT_ARGS = "bundle_message_format_args"
    private const val BUNDLE_POSITIVE_BUTTON_RES_ID = "bundle_positive_button_res_id"
    private const val BUNDLE_NEUTRAL_BUTTON_RES_ID = "bundle_neutral_button_res_id"
    private const val BUNDLE_NEGATIVE_BUTTON_RES_ID = "bundle_negative_button_res_id"
    private const val BUNDLE_DISMISS_ALLOWED = "bundle_dismiss_allowed"
    
    fun create(
        targetController: Controller,
        requestCode: Int, @StringRes titleId: Int, @StringRes messageId: Int, @StringRes positiveButtonId: Int, @StringRes neutralButtonId: Int = 0, @StringRes negativeButtonId: Int = 0,
        dismissAllowed: Boolean = true,
        optionalTitleFormatArgs: List<String>? = null,
        optionalMessageFormatArgs: List<String>? = null
    ): DialogController {
      val args = Bundle().apply {
        putString(BUNDLE_TARGET_INSTANCE_ID, targetController.instanceId)
        putInt(BUNDLE_REQUEST_CODE, requestCode)
        putInt(BUNDLE_TITLE_RES_ID, titleId)
        optionalTitleFormatArgs?.let {
          putStringArrayList(
            BUNDLE_TITLE_FORMAT_ARGS, ArrayList(optionalTitleFormatArgs)
          )
        }
        optionalMessageFormatArgs?.let {
          putStringArrayList(
            BUNDLE_MESSAGE_FORMAT_ARGS, ArrayList(optionalMessageFormatArgs)
          )
        }
        putInt(BUNDLE_MESSAGE_RES_ID, messageId)
        putInt(BUNDLE_POSITIVE_BUTTON_RES_ID, positiveButtonId)
        putInt(BUNDLE_NEUTRAL_BUTTON_RES_ID, neutralButtonId)
        putInt(BUNDLE_NEGATIVE_BUTTON_RES_ID, negativeButtonId)
        putBoolean(BUNDLE_DISMISS_ALLOWED, dismissAllowed)
      }
      
      if (targetController !is Listener) {
        Timber.e("TargetController [$targetController] does not implement BaseDialogController.Listener")
      }
      
      return DialogController(args)
    }
  }
}