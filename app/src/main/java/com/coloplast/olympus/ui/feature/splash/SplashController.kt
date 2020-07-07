package com.coloplast.olympus.ui.feature.splash

import android.os.Handler
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerSplashBinding
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.extensions.fadeIn
import com.coloplast.olympus.ui.common.extensions.rotate360
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.onboarding.LandingController
import org.koin.core.inject

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 25/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class SplashController : BaseController<ControllerSplashBinding>() {

  private val preferences: Preferences by inject()

  override fun attachVariables(viewBinding: ControllerSplashBinding) {
    //noop
  }

  override fun onViewBound(viewBinding: ControllerSplashBinding) {
    viewBinding.appTitle.fadeIn(animationDuration = 200, offset = 200) //0 - 400ms
    viewBinding.coloplastLogo.fadeIn(animationDuration = 200, offset = 400) // 400 - 600ms
    viewBinding.appLogo.rotate360(offset = 600) // 600 - 2600ms

    Handler().postDelayed({ goToNextScreen() }, 2800)
  }

  private fun goToNextScreen(){
    val routerTransaction = if(preferences.userToken.isNotBlank()){
      HomeController.routerTransaction()
    } else {
      // TODO: Link with CPO-518
      LandingController.routerTransaction()
    }

    getContentRouter().setRoot(routerTransaction)
  }

  override fun getLayout(): Int = R.layout.controller_splash

  companion object {
    const val TAG_CONTROLLER = "splash_controller"

    fun routerTransaction() = RouterTransaction.with(SplashController())
      .tag(TAG_CONTROLLER)
  }
}