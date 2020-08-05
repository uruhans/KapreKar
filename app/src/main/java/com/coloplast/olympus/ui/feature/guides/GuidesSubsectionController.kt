package com.coloplast.olympus.ui.feature.guides

import android.os.Bundle
import android.view.View
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
import me.tatarka.bindingcollectionadapter2.OnItemBind

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 20/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class GuidesSubsectionController(args: Bundle): BaseController<ControllerGuidesSubsectionBinding>(args) {

  val type: GuidesMenuSubsection = args.get(GUIDE_TYPE) as GuidesMenuSubsection

  val items = ObservableArrayList<GuideCard>().apply {
    when(type){
      GuidesMenuSubsection.GETTING_STARTED -> {
        add(GuideCard.COMPONENT_GUIDE)
        add(GuideCard.APP_TOUR)
      }
      GuidesMenuSubsection.APPLYING_CHANGING -> {
        add(GuideCard.APPLYING_GUIDE)
        add(GuideCard.DRY_APPLYING_TIPS)
      }
      GuidesMenuSubsection.CONNECTION_AND_TRANSMITTER -> {
        add(GuideCard.CONNECTION_GUIDE)
        add(GuideCard.CHARGING_GUIDE)
        add(GuideCard.LOCATION_GUIDE)
        add(GuideCard.CHECK_BATTERY_LEVEL)
        add(GuideCard.ADD_TRANSMITTER)
        add(GuideCard.DELETE_TRANSMITTER)
      }
    }
  }

  val itemBinding = OnItemBind<GuideCard> { itemBinding, _, item ->
    itemBinding.set(BR.item, R.layout.item_guide_card)
      .bindExtra(BR.listener, object: View.OnClickListener{
        override fun onClick(p0: View?) {
          goToFlow(item)
        }
      })
  }


  override fun attachVariables(viewBinding: ControllerGuidesSubsectionBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerGuidesSubsectionBinding) {
    //noop
  }

  fun goToFlow(guideCard: GuideCard){
    val routerTransaction = when(guideCard){
      GuideCard.COMPONENT_GUIDE -> SplashController.routerTransaction()
      GuideCard.APP_TOUR -> SplashController.routerTransaction()
      GuideCard.APPLYING_GUIDE -> SplashController.routerTransaction()
      GuideCard.DRY_APPLYING_TIPS -> SplashController.routerTransaction()
      GuideCard.CONNECTION_GUIDE -> SplashController.routerTransaction()
      GuideCard.CHARGING_GUIDE -> SplashController.routerTransaction()
      GuideCard.LOCATION_GUIDE -> SplashController.routerTransaction()
      GuideCard.CHECK_BATTERY_LEVEL -> SplashController.routerTransaction()
      GuideCard.ADD_TRANSMITTER -> SplashController.routerTransaction()
      GuideCard.DELETE_TRANSMITTER -> SplashController.routerTransaction()
    }
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
      return RouterTransaction.with(GuidesSubsectionController(args))
        .tag(TAG_CONTROLLER)
    }
  }
}