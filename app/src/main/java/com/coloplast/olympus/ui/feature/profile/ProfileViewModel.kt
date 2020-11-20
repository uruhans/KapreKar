package com.coloplast.olympus.ui.feature.profile

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.rx.DelayedLoadingTransformer
import com.coloplast.olympus.ui.feature.profile.model.ProfileUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class ProfileViewModel(
  private val preferences: Preferences,
  private val userComponent: UserComponent,
  private val me: Me
) : BaseViewModel<ProfileUiModel, ProfileView>() {

  override fun bind(uiView: ProfileView): Disposable {
    val disposable = CompositeDisposable()

    val onUserDataRequested: Observable<ProfileUiModel> =
      userComponent.getLocalUser(UUID.fromString(preferences.userLoggedIn))
        .compose(DelayedLoadingTransformer<User>())
        .map {
          if (it.result != null) {
            ProfileUiModel.FetchedUser(it.result!!)
          }
          else {
            ProfileUiModel.LoadingData()
          }
        }

    val updateUserObservable = uiView.onUpdateUserRequest()
      .switchMap { user ->
        userComponent.updateUser(user)
          .subscribeOn(Schedulers.io())
          .flatMap {
            userComponent.syncUser(user.id!!)
              .map<ProfileUiModel> { ProfileUiModel.UserUpdated(user) }
              .onErrorReturn { ProfileUiModel.UpdateFailed() }
          }
      }

    val onLogoutRequested =
      uiView.onLogoutRequested()
        .map { me.logout(true) }
        .subscribe()

    disposable.add(onLogoutRequested)
    disposable.add(Observable.merge(onUserDataRequested, updateUserObservable).bind(uiView.render()))
    return disposable
  }
}