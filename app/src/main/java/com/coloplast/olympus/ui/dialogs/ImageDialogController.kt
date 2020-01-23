package com.coloplast.olympus.ui.dialogs

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.bluelinelabs.conductor.Controller
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerDialogImageBinding
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import timber.log.Timber

class ImageDialogController(args: Bundle) : BaseDialogController<ControllerDialogImageBinding>(args) {
  
  val titleResId = ObservableInt(R.string.generic_error)
  val messageResId = ObservableInt(R.string.generic_error_message)
  
  val positiveButtonTextResId = ObservableInt()
  val negativeButtonTextResId = ObservableInt(0)
  
  val showNegativeButton = ObservableBoolean()
  
  val topImage = ObservableInt(0)
  
  val verticalButtons: Boolean
  val showTopImage: Boolean
  
  private val dismissAllowed: Boolean
  private val requestCode: Int
  
  init {
    titleResId.set(args.getInt(BUNDLE_TITLE_RES_ID))
    messageResId.set(args.getInt(BUNDLE_MESSAGE_RES_ID))
    
    positiveButtonTextResId.set(args.getInt(BUNDLE_POSITIVE_BUTTON_RES_ID))
    negativeButtonTextResId.set(args.getInt(BUNDLE_NEGATIVE_BUTTON_RES_ID))
    
    topImage.set(args.getInt(BUNDLE_SHOW_IMAGE))
    
    showNegativeButton.set(negativeButtonTextResId.get() != 0)
    showTopImage = topImage.get() != 0
    verticalButtons = args.getBoolean(BUNDLE_VERTICAL_BUTTONS)
    dismissAllowed = args.getBoolean(BUNDLE_DISMISS_ALLOWED)
    requestCode = args.getInt(BUNDLE_REQUEST_CODE)
  }
  
  override fun attachVariables(viewBinding: ControllerDialogImageBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerDialogImageBinding) {
    // no-op
  }
  
  fun onPositiveButtonClicked() {
    getListener(Listener::class.java)?.onPositiveButtonClicked(requestCode)
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
    return R.layout.controller_dialog_image
  }
  
  companion object {
    private const val BUNDLE_REQUEST_CODE = "bundle_request_code"
    private const val BUNDLE_TITLE_RES_ID = "bundle_title_res_id"
    private const val BUNDLE_MESSAGE_RES_ID = "bundle_message_res_id"
    private const val BUNDLE_POSITIVE_BUTTON_RES_ID = "bundle_positive_button_res_id"
    private const val BUNDLE_NEGATIVE_BUTTON_RES_ID = "bundle_negative_button_res_id"
    private const val BUNDLE_SHOW_IMAGE = "bundle_show_image"
    private const val BUNDLE_VERTICAL_BUTTONS = "bundle_vertical_buttons"
    private const val BUNDLE_DISMISS_ALLOWED = "bundle_dismiss_allowed"
    
    fun create(
        targetController: Controller,
        requestCode: Int,
        @StringRes titleId: Int,
        @StringRes messageId: Int,
        @StringRes positiveButtonId: Int,
        @StringRes negativeButtonId: Int = 0,
        @DrawableRes topImage: Int = 0,
        verticalButtonLayout: Boolean = false,
        dismissAllowed: Boolean = true
    ): ImageDialogController {
      val args = Bundle().apply {
        putString(BUNDLE_TARGET_INSTANCE_ID, targetController.instanceId)
        putInt(BUNDLE_REQUEST_CODE, requestCode)
        putInt(BUNDLE_TITLE_RES_ID, titleId)
        putInt(BUNDLE_MESSAGE_RES_ID, messageId)
        putInt(BUNDLE_POSITIVE_BUTTON_RES_ID, positiveButtonId)
        putInt(BUNDLE_NEGATIVE_BUTTON_RES_ID, negativeButtonId)
        putInt(BUNDLE_SHOW_IMAGE, topImage)
        putBoolean(BUNDLE_VERTICAL_BUTTONS, verticalButtonLayout)
        putBoolean(BUNDLE_DISMISS_ALLOWED, dismissAllowed)
      }
      
      if (targetController !is Listener) {
        Timber.e("TargetController [$targetController] does not implement BaseDialogController.Listener")
      }
      
      return ImageDialogController(args)
    }
  }
  
}