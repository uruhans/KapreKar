package com.coloplast.olympus.ui.feature.splash

import android.os.Handler
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerSplashBinding
import com.coloplast.olympus.shared.preferences.OnboardingProgression
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.extensions.fadeIn
import com.coloplast.olympus.ui.common.extensions.rotate360
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.feature.home.HomeController
import com.coloplast.olympus.ui.feature.onboarding.LandingController
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject
import java.util.*

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 25/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class SplashController : BaseController<ControllerSplashBinding>(), SplashView {

  private val viewModel: SplashViewModel by inject()
  private val preferences: Preferences by inject()

  private val userIdRelay = PublishRelay.create<UUID>()

  override fun attachVariables(viewBinding: ControllerSplashBinding) {
    //noop
  }

  override fun onViewBound(viewBinding: ControllerSplashBinding) {
    viewBinding.appTitle.fadeIn(animationDuration = 200, offset = 200) //0 - 400ms
    viewBinding.coloplastLogo.fadeIn(animationDuration = 200, offset = 400) // 400 - 600ms
    viewBinding.appLogo.rotate360(offset = 600) // 600 - 2600ms

    if(preferences.userLoggedIn.isBlank()) {
      Handler().postDelayed({ goToNextScreen() }, 2800)
    }
    else {
      compositeDisposable.add(viewModel.bind(this))

      userIdRelay.accept(UUID.fromString(preferences.userLoggedIn))
    }
  }

  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui {

      // we dont really care what the result is
      // we just continue
      goToNextScreen()
    }
  }

  override fun userIdForLoggedInUser(): Observable<UUID> {
    return userIdRelay.publish().refCount()
  }

  private fun goToNextScreen() {
    val routerTransaction = when {
      preferences.userToken.isNotBlank() && preferences.getCurrentOnboardingProgress() == OnboardingProgression.COMPLETED -> {
        HomeController.routerTransaction()
      }
      else -> {
        LandingController.routerTransaction()
      }
    }

    routerTransaction.let {
      getContentRouter().setRoot(it)
    }
  }

  override fun getLayout(): Int = R.layout.controller_splash

  companion object {
    const val TAG_CONTROLLER = "splash_controller"

    fun routerTransaction() = RouterTransaction.with(SplashController())
      .tag(TAG_CONTROLLER)
  }
}