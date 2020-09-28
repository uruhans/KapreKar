package com.coloplast.olympus.ui.feature.profile

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.rx.DelayedLoadingTransformer
import com.coloplast.olympus.ui.common.rx.GenericUiModelTransformer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 08/09/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class ProfileViewModel(private val preferences: Preferences, private val userComponent: UserComponent, private val me: Me) :
  BaseViewModel<GenericUiModel, ProfileView>() {

  override fun bind(uiView: ProfileView): Disposable {
    val disposable = CompositeDisposable()

    val onUserDataRequested: Observable<GenericUiModel> =
      userComponent.getLocalUser(UUID.fromString(preferences.userLoggedIn))
        .compose(DelayedLoadingTransformer<User>())
        .compose(GenericUiModelTransformer())

    val onLogoutRequested =
      uiView.onLogoutRequested()
        .map { me.logout(true) }
        .subscribe()

    disposable.add(onLogoutRequested)
    disposable.add(onUserDataRequested.bind(uiView.render()))
    return disposable
  }
}