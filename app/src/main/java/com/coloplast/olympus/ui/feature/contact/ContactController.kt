package com.coloplast.olympus.ui.feature.contact

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerContactBinding
import com.coloplast.olympus.ui.common.CustomToolbarController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 09/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ContactController: CustomToolbarController<ControllerContactBinding>() {

  override fun attachVariables(viewBinding: ControllerContactBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun getLayout(): Int = R.layout.controller_contact

  override fun getNavigationIconResId() = R.drawable.ic_arrow

  companion object{
    private const val TAG_CONTROLLER = "contact_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(ContactController()).tag(TAG_CONTROLLER)
    }
  }
}