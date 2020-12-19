package com.coloplast.olympus.ui.feature.uistates

import android.view.View
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
    val onBoardedLessThan48Hours = (System.currentTimeMillis() - preferences.onboardingCompletedTime) / MILLIS_TO_HOURS < 48
    shouldShowAppTourButton.set(onBoardedLessThan48Hours && !preferences.didSeeAppTourGuide)
  }

  fun startAppTour() {
    preferences.didSeeAppTourGuide = true
    getContentRouter().pushController(
      AppTourFlowController.routerTransaction()
        .pushChangeHandler(HorizontalChangeHandler())
        .popChangeHandler(HorizontalChangeHandler())
    )
  }

  companion object {
    private const val MILLIS_TO_HOURS = 1000L * 60 * 60
  }
}

