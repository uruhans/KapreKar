package com.coloplast.olympus.ui.common

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerInfoBinding
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener

class InfoPageController(args: Bundle) : BaseController<ControllerInfoBinding>(args),
  TopMenuVisibilityListener {

  val iconId = args.getInt(INFO_ICON)
  val titleId = args.getInt(INFO_TITLE)
  val descriptionId = args.getInt(INFO_DESCRIPTION)
  val positiveButtonTextId = args.getInt(POSITIVE_TEXT_ID)
  val negativeButtonTextId = args.getInt(NEGATIVE_TEXT_ID)
  val showBackButton = args.getBoolean(INFO_SHOW_BACK_BUTTON)
  val showCloseButton = args.getBoolean(INFO_SHOW_CLOSE_BUTTON)

  val showNegativeButton = negativeButtonTextId > 0

  override fun attachVariables(viewBinding: ControllerInfoBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerInfoBinding) {
    //noop
  }

  fun onPositiveButtonClicked() {
    getListener(FlowListener::class.java)?.onNextClicked(true)
  }

  fun onNegativeButtonClicked() {
    getListener(FlowListener::class.java)?.onNextClicked(false)
  }

  override fun showBackButton(): Boolean = showBackButton
  override fun showCloseButton(): Boolean = showCloseButton

  override fun getLayout(): Int {
    return R.layout.controller_info
  }

  companion object {
    private const val TAG_CONTROLLER = "simple_page_controller"

    private const val INFO_ICON = "INFO_ICON"
    private const val INFO_TITLE = "INFO_TITLE"
    private const val INFO_DESCRIPTION = "INFO_DESCRIPTION"
    private const val POSITIVE_TEXT_ID = "POSITIVE_TEXT_ID"
    private const val NEGATIVE_TEXT_ID = "NEGATIVE_TEXT_ID"
    private const val INFO_SHOW_BACK_BUTTON = "INFO_SHOW_BACK_BUTTON"
    private const val INFO_SHOW_CLOSE_BUTTON = "INFO_SHOW_CLOSE_BUTTON"

    fun routerTransaction(
      @DrawableRes iconId: Int,
      @StringRes titleId: Int,
      @StringRes descriptionId: Int,
      @StringRes positiveButtonTextId: Int,
      @StringRes negativeButtonTextId: Int = 0,
      showBackButton: Boolean = true,
      showCloseButton: Boolean = true
    ): RouterTransaction {
      val args = Bundle().apply {
        putInt(INFO_ICON, iconId)
        putInt(INFO_TITLE, titleId)
        putInt(INFO_DESCRIPTION, descriptionId)
        putInt(POSITIVE_TEXT_ID, positiveButtonTextId)
        putInt(NEGATIVE_TEXT_ID, negativeButtonTextId)
        putBoolean(INFO_SHOW_BACK_BUTTON, showBackButton)
        putBoolean(INFO_SHOW_CLOSE_BUTTON, showCloseButton)
      }
      return RouterTransaction.with(InfoPageController(args))
        .tag(TAG_CONTROLLER)
    }
  }
}