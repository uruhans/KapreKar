package com.coloplast.olympus.ui.feature.profile

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.ServiceStateDispatcher
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.preferences.PreferencesImpl
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.rx.DelayedLoadingTransformer
import com.coloplast.olympus.ui.feature.profile.model.ProfileUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class ProfileViewModel(
  private val preferences: PreferencesImpl,
  private val userComponent: UserComponent,
  private val me: Me,
  private val bluetoothManager: BluetoothManager,
  private val serviceStateDispatcher: ServiceStateDispatcher
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
        .flatMap {
          userComponent.clearLocalStorage()
            .subscribeOn(Schedulers.io())
            // disconnect all transmitters
            .map { bluetoothManager.disconnectAllTransmitters() }
            // stop the service to make sure it doesn't try to automatically reconnect to any transmitters
            .map { serviceStateDispatcher.stopService() }
            // log out user -> will move user to landing page
            .map { me.logout(true) }
        }

    disposable.add(onLogoutRequested.subscribe())
    disposable.add(Observable.merge(onUserDataRequested, updateUserObservable).bind(uiView.render()))
    return disposable
  }
}