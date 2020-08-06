package com.coloplast.olympus.ui.feature.guides

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuidesMenuBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.types.GuidesMenuSubsection
import me.tatarka.bindingcollectionadapter2.ItemBinding


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 17/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class GuideMenuController: BaseController<ControllerGuidesMenuBinding>() {

  val items = ObservableArrayList<GuidesMenuSubsection>().apply {
    addAll(GuidesMenuSubsection.values())
  }

  val itemBinding = ItemBinding.of<GuidesMenuSubsection>(BR.type, R.layout.item_guide_menu)
    .bindExtra(BR.controller, this)

  override fun attachVariables(viewBinding: ControllerGuidesMenuBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerGuidesMenuBinding) {
    //noop
  }

  fun goToDestination(guidesMenuSubsection: GuidesMenuSubsection){
    val routerTransaction = GuidesSubsectionController.getRouterTransaction(guidesMenuSubsection)
    router.pushController(routerTransaction
      .pushChangeHandler(HorizontalChangeHandler())
      .popChangeHandler(HorizontalChangeHandler()))
  }

  override fun getLayout(): Int = R.layout.controller_guides_menu

  companion object{
    private const val CONTROLLER_TAG = "guides_menu_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(GuideMenuController())
        .tag(CONTROLLER_TAG)
    }
  }
}