package com.coloplast.olympus.ui.feature.guides

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuidesMenuBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.models.MenuItem
import com.coloplast.olympus.ui.feature.splash.SplashController
import me.tatarka.bindingcollectionadapter2.ItemBinding


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 17/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class GuideMenuController: BaseController<ControllerGuidesMenuBinding>() {

  val items = ObservableArrayList<MenuItem>().apply {
    add(MenuItem(R.drawable.ic_opening_door, R.string.guides_menu_getting_started, SplashController.routerTransaction())) //TODO: Set destination when the screen is ready.
    add(MenuItem(R.drawable.ic_sensor_layer, R.string.guides_menu_applying_changing, SplashController.routerTransaction())) //TODO: Set destination when the screen is ready.
    add(MenuItem(R.drawable.ic_transmitter, R.string.guides_menu_connection_transmitter, SplashController.routerTransaction())) //TODO: Set destination when the screen is ready.
    add(MenuItem(R.drawable.ic_maintenance, R.string.guides_menu_maintenance, SplashController.routerTransaction())) //TODO: Set destination when the screen is ready.
    add(MenuItem(R.drawable.ic_faq, R.string.guides_menu_faq, SplashController.routerTransaction())) //TODO: Set destination when the screen is ready.
  }

  val itemBinding = ItemBinding.of<MenuItem>(BR.item, R.layout.item_guide_menu)
    .bindExtra(BR.controller, this)

  override fun attachVariables(viewBinding: ControllerGuidesMenuBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerGuidesMenuBinding) {
    //noop
  }

  fun goToDestination(routerTransaction: RouterTransaction){
    getContentRouter().pushController(routerTransaction
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