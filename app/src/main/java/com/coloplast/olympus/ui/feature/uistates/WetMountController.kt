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

  val headLineTxt = ObservableField<String>()
  val descriptionTxt = ObservableField<String>()

  private var isWetMountTypeG = args.getBoolean(BUNDLE_WET_MOUNT_TYPE)

  override fun attachVariables(viewBinding: ControllerStateWetMountBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStateWetMountBinding) {
    if (isWetMountTypeG) {
      headLineTxt.set(getString(R.string.status_wet_mount_title) as String?)
      descriptionTxt.set(getString(R.string.status_wet_mount) as String?)
    }
    else {
      headLineTxt.set(getString(R.string.status_inner_blind_mount_title) as String?)
      descriptionTxt.set(getString(R.string.status_inner_blind_mount_description) as String?)
    }
  }

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
    private const val BUNDLE_WET_MOUNT_TYPE = "bundle_wet_mount_type"

    fun routerTransaction(isWetMountTypeG: Boolean): RouterTransaction {
      Bundle().apply {
        putBoolean(BUNDLE_WET_MOUNT_TYPE, isWetMountTypeG)
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