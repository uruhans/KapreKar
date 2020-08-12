package com.coloplast.olympus.ui.common

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerInfoSmallButtonBinding
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener

class InfoPageSmallButtonController(args: Bundle) :
  BaseController<ControllerInfoSmallButtonBinding>(args),
  TopMenuVisibilityListener {

  val iconId = args.getInt(INFO_ICON)
  val titleId = args.getInt(INFO_TITLE)
  val descriptionId = args.getInt(INFO_DESCRIPTION)
  val showBackButton = args.getBoolean(INFO_SHOW_BACK_BUTTON)
  val showCloseButton = args.getBoolean(INFO_SHOW_CLOSE_BUTTON)

  override fun attachVariables(viewBinding: ControllerInfoSmallButtonBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerInfoSmallButtonBinding) {
    //noop
  }

  fun onNextClicked() {
    getListener(FlowListener::class.java)?.onNextClicked(true)
  }

  override fun showBackButton(): Boolean = showBackButton
  override fun showCloseButton(): Boolean = showCloseButton

  override fun getLayout(): Int {
    return R.layout.controller_info_small_button
  }

  companion object {
    private const val TAG_CONTROLLER = "info_page_small_button_controller"

    private const val INFO_ICON = "INFO_ICON"
    private const val INFO_TITLE = "INFO_TITLE"
    private const val INFO_DESCRIPTION = "INFO_DESCRIPTION"
    private const val INFO_SHOW_BACK_BUTTON = "INFO_SHOW_BACK_BUTTON"
    private const val INFO_SHOW_CLOSE_BUTTON = "INFO_SHOW_CLOSE_BUTTON"

    fun routerTransaction(
      @DrawableRes iconId: Int,
      @StringRes titleId: Int,
      @StringRes descriptionId: Int,
      showBackButton: Boolean = true,
      showCloseButton: Boolean = true
    ): RouterTransaction {
      val args = Bundle().apply {
        putInt(INFO_ICON, iconId)
        putInt(INFO_TITLE, titleId)
        putInt(INFO_DESCRIPTION, descriptionId)
        putBoolean(INFO_SHOW_BACK_BUTTON, showBackButton)
        putBoolean(INFO_SHOW_CLOSE_BUTTON, showCloseButton)
      }
      return RouterTransaction.with(InfoPageSmallButtonController(args))
        .tag(TAG_CONTROLLER)
    }
  }
}