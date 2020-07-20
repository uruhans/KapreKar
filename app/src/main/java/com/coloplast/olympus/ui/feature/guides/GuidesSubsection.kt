package com.coloplast.olympus.ui.feature.guides

import android.os.Bundle
import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuidesSubsectionBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.models.GuideCard
import com.coloplast.olympus.ui.feature.guides.types.GuidesMenuSubsection
import com.coloplast.olympus.ui.feature.splash.SplashController
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 20/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class GuidesSubsection(args: Bundle): BaseController<ControllerGuidesSubsectionBinding>(args) {

  val type: GuidesMenuSubsection = args.get(GUIDE_TYPE) as GuidesMenuSubsection

  val items = ObservableArrayList<GuideCard>().apply {
    when(type){
      GuidesMenuSubsection.GETTING_STARTED -> {
        add(GuideCard(
          R.drawable.guide_wiping_2,
          R.string.guides_co_card_title,
          R.string.guides_co_card_description,
          SplashController.routerTransaction() //TODO: Set destination when the screen is ready.
        ))
        add(GuideCard(
          R.drawable.guide_wiping_2,
          R.string.guides_ap_card_title,
          R.string.guides_ap_card_description,
          SplashController.routerTransaction() //TODO: Set destination when the screen is ready.
        ))
      }
    }

  }

  val itemBinding = ItemBinding.of<GuideCard>(BR.item, R.layout.item_guide_card)
    .bindExtra(BR.controller, this)

  override fun attachVariables(viewBinding: ControllerGuidesSubsectionBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerGuidesSubsectionBinding) {
    //noop
  }

  fun goToDestination(routerTransaction: RouterTransaction){
    getContentRouter().pushController(routerTransaction
      .pushChangeHandler(HorizontalChangeHandler())
      .popChangeHandler(HorizontalChangeHandler()))
  }

  override fun getLayout(): Int = R.layout.controller_guides_subsection

  companion object {
    const val TAG_CONTROLLER = "guides_subsection_controller"
    const val GUIDE_TYPE = "GUIDE_TYPE"

    fun getRouterTransaction(guidesMenuSubsection: GuidesMenuSubsection): RouterTransaction{
      val args = Bundle().apply {
        putSerializable(GUIDE_TYPE, guidesMenuSubsection)
      }
      return RouterTransaction.with(GuidesSubsection(args))
        .tag(TAG_CONTROLLER)
    }
  }
}