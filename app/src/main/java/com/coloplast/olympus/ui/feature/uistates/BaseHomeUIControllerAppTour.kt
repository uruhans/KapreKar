package com.coloplast.olympus.ui.feature.uistates

import androidx.databinding.ObservableBoolean
import androidx.databinding.ViewDataBinding
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.feature.guides.flows.tour.AppTourFlowController
import org.koin.core.inject

abstract class BaseHomeUIControllerAppTour<T : ViewDataBinding>() : BaseHomeUIController<T>() {

  private val preferences: Preferences by inject()

  val shouldShowAppTourButton = ObservableBoolean(false)

  init {
    shouldShowAppTourButton.set(false)
    //shouldShowAppTourButton.set(!preferences.didSeeAppTourGuide)
  }

//  val shouldShowAppTourButton: Boolean
//    get() {
//      return show
//    }
//    val onBoardedLessThan48Hours = (System.currentTimeMillis() - preferences.getOnboardingCompletedTimeStamp()) / (1000L * 60 * 60) < 48
//    !preferences.didSeeAppTourGuide //&& !onBoardedLessThan48Hours

  fun startAppTour() {
    preferences.didSeeAppTourGuide = true
    val (selectedRouter, routerTransaction) = Pair(getContentRouter(), AppTourFlowController.routerTransaction())
    selectedRouter.pushController(routerTransaction
      .pushChangeHandler(HorizontalChangeHandler())
      .popChangeHandler(HorizontalChangeHandler()))
  }
}