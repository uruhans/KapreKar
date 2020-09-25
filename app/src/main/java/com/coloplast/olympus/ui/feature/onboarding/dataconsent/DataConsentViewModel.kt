package com.coloplast.olympus.ui.feature.onboarding.dataconsent

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.rx.DelayedLoadingTransformer
import com.coloplast.olympus.ui.common.rx.GenericUiModelTransformer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 24/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class DataConsentViewModel(
  private val userComponent: UserComponent,
  private val preferences: Preferences,
  private val me: Me
) : BaseViewModel<GenericUiModel, DataConsentView>() {

  override fun bind(uiView: DataConsentView): Disposable {
    val disposable = CompositeDisposable()

    val onAcceptedDataPolicy: Observable<GenericUiModel> =
      uiView.onUpdateDataConsent()
        .switchMap { dataConsent ->
          //TODO: Get and update the data from the DB
          me.currentUserObservable()
            .filter { it is CurrentUser.LoggedIn }
            .flatMap {
              val user = (it as CurrentUser.LoggedIn).user.apply {
                consent = dataConsent
              }
              return@flatMap userComponent.updateUser(user)
            }
            .map { true }
            .compose(DelayedLoadingTransformer<Boolean>())
            .compose(GenericUiModelTransformer())
        }

    disposable.add(onAcceptedDataPolicy.bind(uiView.render()))
    return disposable
  }

}