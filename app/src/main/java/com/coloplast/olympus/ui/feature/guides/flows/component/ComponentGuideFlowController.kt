package com.coloplast.olympus.ui.feature.guides.flows.component

import android.content.Intent
import android.net.Uri
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.MediaGuideController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.common.models.flow.EmptyModel
import com.coloplast.olympus.ui.dialogs.CallHelpDialogController


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 03/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ComponentGuideFlowController: TopMenuFlowFactoryController<EmptyModel>(),
  MediaGuideController.Listener,
  CallHelpDialogController.Listener {

  private var callHelpDialogController: CallHelpDialogController? = null
  private val model: FlowFactory<EmptyModel> by lazy { ComponentGuideFactoryImpl() }

  override fun getFlowFactory(): FlowFactory<EmptyModel> = model

  override fun onQuestionClicked(screen: String) {
    callHelpDialogController = CallHelpDialogController.create(
      this,
      DIALOG_CALL_HELP,
      true
    )
    callHelpDialogController?.show()
  }

  override fun onDialogDismissed(requestCode: Int) {
    if(requestCode == DIALOG_CALL_HELP) {
      callHelpDialogController = null
    }
  }

  override fun onCallClicked(requestCode: Int, number: String) {
    if(requestCode == DIALOG_CALL_HELP) {
      Intent(Intent.ACTION_DIAL).also {
        it.data = Uri.parse("tel:$number")
        startActivity(it)
      }
    }
  }

  companion object{
    private const val CONTROLLER_TAG = "component_guide_flow_controller"
    private const val DIALOG_CALL_HELP = 1005

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(ComponentGuideFlowController())
        .tag(CONTROLLER_TAG)
    }
  }

}