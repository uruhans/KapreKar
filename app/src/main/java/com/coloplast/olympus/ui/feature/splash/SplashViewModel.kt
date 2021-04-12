package com.coloplast.olympus.ui.feature.splash

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.preferences.PreferencesImpl
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import com.coloplast.olympus.ui.common.models.UiView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class SplashViewModel(
  private val me: Me,
  private val userComponent: UserComponent,
  private val preferences: PreferencesImpl
) : BaseViewModel<GenericUiModel, UiView<GenericUiModel>>() {

  override fun bind(uiView: UiView<GenericUiModel>): Disposable {
    val disposable = CompositeDisposable()

    val automaticLoginObservable =
      me.currentUserObservable()
        .flatMap {
          if (it is CurrentUser.LoggedIn) {
            Observable.just(GenericUiModel.Success(it))
          }
          else {
            if (preferences.userLoggedIn.isEmpty()) {
              Observable.just(GenericUiModel.Error(GenericUiModel.Error.State.ERROR_GENERIC, 10000))
            }
            else {
              val userId = UUID.fromString(preferences.userLoggedIn)

              userComponent.automaticLogin(userId)
                .subscribeOn(Schedulers.io())
                .map { currentUser ->
                  me.setCurrentUser(currentUser, false)

                  if (currentUser is CurrentUser.LoggedIn) {
                    GenericUiModel.Success(currentUser)
                  }
                  else {
                    me.logout()
                    GenericUiModel.Error(GenericUiModel.Error.State.ERROR_GENERIC, 404)
                  }
                }
            }
          }
        }
        .onErrorReturn {
          me.logout()
          GenericUiModel.Error(GenericUiModel.Error.State.ERROR_GENERIC, 404)
        }
        // delay for fun - splash animation must complete
        .delay(3, TimeUnit.SECONDS)

    disposable.add(automaticLoginObservable.bind(uiView.render()))
    return disposable
  }
}