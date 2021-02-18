package com.coloplast.olympus.ui.feature.uistates

import android.os.Bundle
import androidx.databinding.ObservableField
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStateWetMountBinding
import com.coloplast.olympus.ui.feature.guides.SingleGuideFlowController
import com.coloplast.olympus.ui.feature.guides.articles.models.GuideType
import com.coloplast.olympus.ui.feature.home.models.PatchVisible

class WetMountController(args: Bundle) : BaseHomeUIController<ControllerStateWetMountBinding>(), PatchVisible {

  val headLineTxt = ObservableField<String>(args.getString(BUNDLE_HEADLINE_STRING_ID))
  val descriptionTxt = ObservableField<String>(args.getString(BUNDLE_DESCRIPTION_STRING_ID))

  override fun attachVariables(viewBinding: ControllerStateWetMountBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStateWetMountBinding) {  }

  fun onGuideClicked() {
    getContentRouter().pushController(
      SingleGuideFlowController.routerTransaction(GuideType.UNDERSTANDING_SENSORS)
        .pushChangeHandler(VerticalChangeHandler())
        .popChangeHandler(VerticalChangeHandler())
    )
  }

  override fun patchInError(): Boolean {
    return true
  }

  override fun getLayout(): Int {
    return R.layout.controller_state_wet_mount
  }

  companion object {
    private const val TAG_CONTROLLER = "state_wet_mount_controller"
    private const val BUNDLE_HEADLINE_STRING_ID = "bundle_headline_string_id"
    private const val BUNDLE_DESCRIPTION_STRING_ID = "bundle_description_string_id"

    fun routerTransaction(headline: String, description: String): RouterTransaction {
      Bundle().apply {
        putString(BUNDLE_HEADLINE_STRING_ID, headline)
        putString(BUNDLE_DESCRIPTION_STRING_ID, description)
      }
        .also {
          return RouterTransaction.with(
            WetMountController(
              it
            )
          ).tag(TAG_CONTROLLER)
        }
    }
  }
}