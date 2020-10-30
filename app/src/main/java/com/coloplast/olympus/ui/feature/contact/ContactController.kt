package com.coloplast.olympus.ui.feature.contact

import android.content.Intent
import android.net.Uri
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerContactBinding
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.DrawerStateDispatcher


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 09/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ContactController: CustomToolbarController<ControllerContactBinding>() {

  override fun attachVariables(viewBinding: ControllerContactBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  fun onCallClicked(){
    val callIntent = Intent().apply {
      action = Intent.ACTION_DIAL
      data = Uri.parse("tel:${getString(R.string.contact_cp_phone_number)}")
    }
    startActivity(callIntent)
  }

  fun onEmailClicked(){
    val emailIntent = Intent().apply {
      action = Intent.ACTION_SENDTO
      data = Uri.fromParts(
        "mailto", getString(R.string.contact_cp_support_email).toString(), null
      )
    }
    startActivity(emailIntent)
  }

  override fun getLayout(): Int = R.layout.controller_contact

  override fun getNavigationIconResId() = R.drawable.ic_arrow

  override fun onCloseClicked() {
    DrawerStateDispatcher.closeNavigationDrawerOnHome()
    super.onCloseClicked()
  }

  companion object{
    private const val TAG_CONTROLLER = "contact_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(ContactController()).tag(TAG_CONTROLLER)
    }
  }
}