package com.coloplast.olympus.ui.common

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerSimpleInfoBinding
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 30/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class SimpleInfoPageController(args: Bundle) : BaseController<ControllerSimpleInfoBinding>(args),
  TopMenuVisibilityListener {

  val iconId = args.getInt(INFO_ICON)
  val titleId = args.getInt(INFO_TITLE)
  val descriptionId = args.getInt(INFO_DESCRIPTION)
  val position = args.getInt(INFO_POSITION)
  val totalCount = args.getInt(INFO_TOTAL_COUNT)
  val showBackButton = args.getBoolean(INFO_SHOW_BACK_BUTTON)
  val showCloseButton = args.getBoolean(INFO_SHOW_CLOSE_BUTTON)

  override fun attachVariables(viewBinding: ControllerSimpleInfoBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerSimpleInfoBinding) {
    //noop
  }

  override fun showBackButton(): Boolean = showBackButton
  override fun showCloseButton(): Boolean = showCloseButton

  fun onNextClicked() = getListener(FlowListener::class.java)!!.onNextClicked(true)

  override fun getLayout(): Int = R.layout.controller_simple_info

  companion object {
    private const val TAG_CONTROLLER = "simple_page_controller"

    private const val INFO_ICON = "INFO_ICON"
    private const val INFO_TITLE = "INFO_TITLE"
    private const val INFO_DESCRIPTION = "INFO_DESCRIPTION"
    private const val INFO_POSITION = "INFO_POSITION"
    private const val INFO_TOTAL_COUNT = "INFO_TOTAL_COUNT"
    private const val INFO_SHOW_BACK_BUTTON = "INFO_SHOW_BACK_BUTTON"
    private const val INFO_SHOW_CLOSE_BUTTON = "INFO_SHOW_CLOSE_BUTTON"

    fun routerTransaction(
      @DrawableRes iconId: Int,
      @StringRes titleId: Int,
      @StringRes desciptionId: Int,
      position: Int,
      totalCount: Int,
      showBackButton: Boolean = true,
      showCloseButton: Boolean = true
    ): RouterTransaction {
      val args = Bundle().apply {
        putInt(INFO_ICON, iconId)
        putInt(INFO_TITLE, titleId)
        putInt(INFO_DESCRIPTION, desciptionId)
        putInt(INFO_POSITION, position)
        putInt(INFO_TOTAL_COUNT, totalCount)
        putBoolean(INFO_SHOW_BACK_BUTTON, showBackButton)
        putBoolean(INFO_SHOW_CLOSE_BUTTON, showCloseButton)
      }
      return RouterTransaction.with(SimpleInfoPageController(args))
        .tag(TAG_CONTROLLER)
    }
  }
}