package com.coloplast.olympus.ui.common

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGenericConfirmationBinding
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 06/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class GenericConfirmationController(args: Bundle): BaseController<ControllerGenericConfirmationBinding>(args) {

  val titleId = args.getInt(TITLE_TEXT_ID)
  val descriptionId = args.getInt(DESCRIPTION_TEXT_ID)
  val buttonTextId = args.getInt(BUTTON_TEXT_ID)
  val imageId = args.getInt(IMAGE_ID)

  override fun attachVariables(viewBinding: ControllerGenericConfirmationBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerGenericConfirmationBinding) {
    //noop
  }

  fun onFinishButtonClicked(){
    getListener(FlowListener::class.java)!!.onNextClicked()
  }

  override fun getLayout(): Int = R.layout.controller_generic_confirmation


  companion object{
    private const val TAG_CONTROLLER = "generic_confirmation_controller"

    private const val TITLE_TEXT_ID = "TITLE_ID"
    private const val DESCRIPTION_TEXT_ID = "DESCRIPTION_TEXT_ID"
    private const val BUTTON_TEXT_ID = "BUTTON_TEXT_ID"
    private const val IMAGE_ID = "IMAGE_ID"

    fun getRouterTransaction(
      @StringRes titleId: Int,
      @StringRes descriptionId: Int,
      @StringRes buttonId: Int,
      @DrawableRes imageId: Int = R.drawable.ic_checkmark
    ): RouterTransaction{
      val args = Bundle().apply {
        putInt(TITLE_TEXT_ID, titleId)
        putInt(DESCRIPTION_TEXT_ID, descriptionId)
        putInt(BUTTON_TEXT_ID,buttonId)
        putInt(IMAGE_ID, imageId)
      }

    return RouterTransaction.with(GenericConfirmationController(args))
      .tag(TAG_CONTROLLER)
    }
  }
}