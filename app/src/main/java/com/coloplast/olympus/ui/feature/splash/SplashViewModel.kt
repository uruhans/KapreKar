package com.coloplast.olympus.ui.feature.splash

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.models.GenericUiModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SplashViewModel(
  private val me: Me,
  private val userComponent: UserComponent
) : BaseViewModel<GenericUiModel, SplashView>() {

  override fun bind(uiView: SplashView): Disposable {
    val disposable = CompositeDisposable()

    val automaticLoginObservable = uiView.userIdForLoggedInUser()
      .switchMap { userId ->
        userComponent.automaticLogin(userId)
          .subscribeOn(Schedulers.io())
          .map<GenericUiModel> { currentUser ->
            me.setCurrentUser(currentUser)

            if (currentUser is CurrentUser.LoggedIn) {
              GenericUiModel.Success(currentUser)
            }
            else {
              me.logout()
              GenericUiModel.Error(GenericUiModel.Error.State.ERROR_GENERIC, 404)
            }
          }
          .onErrorReturn {
            me.logout()
            GenericUiModel.Error(GenericUiModel.Error.State.ERROR_GENERIC, 404)
          }
          .buffer(2800, TimeUnit.MILLISECONDS)
          .map { it.first() }
          .take(1)
      }

    disposable.add(automaticLoginObservable.bind(uiView.render()))
    return disposable
  }
}