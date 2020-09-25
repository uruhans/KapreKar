package com.coloplast.olympus.ui.feature.onboarding.dataconsent

import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.databinding.ControllerFlowBinding
import com.coloplast.olympus.shared.preferences.OnboardingProgression
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.LoadingController
import com.coloplast.olympus.ui.common.flow.FlowFactory
import com.coloplast.olympus.ui.common.flow.TopMenuFlowFactoryController
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 10/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class DataConsentFlowController : TopMenuFlowFactoryController<DataConsentModel>(),
  DataConsentView {

  private val factory by lazy { DataConsentFactoryImpl() }
  private val preferences: Preferences by inject()
  private val viewModel: DataConsentViewModel by inject()
  private val dataConsentRelay: PublishRelay<Boolean> = PublishRelay.create()

  override fun getFlowFactory(): FlowFactory<DataConsentModel> = factory

  override fun onStepCompleted(currentIndex: Int, data: DataConsentModel): Boolean {
    return when (DataConsentFlow.fromIndex(currentIndex)) {
      DataConsentFlow.DataConsentDC1 -> {
        return if (data.skip) {
          with(factory) {
            skipUntil(DataConsentFlow.DataConsentDC5.id)
            incrementAndNext()
          }
          true
        }
        else {
          false // go to next step as usual.
        }
      }
      DataConsentFlow.DataConsentDC5 -> {
        if (data.didConsent) {
          dataConsentRelay.accept(true)
          true
        }
        else {
          false
        }
      }
      else -> super.onStepCompleted(currentIndex, data)
    }
  }

  override fun onFlowCompleted(data: DataConsentModel) {
    preferences.setCurrentOnboardingProgression(OnboardingProgression.SETUP_GUIDE_INTRODUCTION)
    super.onFlowCompleted(data)
  }


  override fun render(): Function<Observable<GenericUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      val isLoading = uiModel is GenericUiModel.Loading
      LoadingController.toggle(isLoading, router)

      when (uiModel) {
        is GenericUiModel.Success<*> -> {
          with(factory) {
            skipUntil(DataConsentFlow.ProfileComplete.id)
            incrementAndNext()
          }
        }
      }
    }
  }

  override fun onViewBound(viewBinding: ControllerFlowBinding) {
    compositeDisposable.add(viewModel.bind(this))
    super.onViewBound(viewBinding)
  }


  override fun onUpdateDataConsent(): Observable<Boolean> {
    return dataConsentRelay
      .publish()
      .refCount()
  }

  companion object {
    private const val CONTROLLER_TAG = "data_consent_flow_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(DataConsentFlowController())
        .tag(CONTROLLER_TAG)
    }
  }
}