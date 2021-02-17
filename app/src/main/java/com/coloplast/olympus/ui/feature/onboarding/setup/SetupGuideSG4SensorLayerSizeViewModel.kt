package com.coloplast.olympus.ui.feature.onboarding.setup

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.models.User
import com.coloplast.olympus.shared.preferences.PreferencesImpl
import com.coloplast.olympus.ui.common.BaseViewModel
import com.coloplast.olympus.ui.common.rx.DelayedLoadingTransformer
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SetupGuideSGC4SensorLayerSizeUiModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.UUID

class SetupGuideSG4SensorLayerSizeViewModel(
  private val preferences: PreferencesImpl,
  private val userComponent: UserComponent
) : BaseViewModel<SetupGuideSGC4SensorLayerSizeUiModel, SetupGuideSG4SensorLayerSizeView>() {

  override fun bind(uiView: SetupGuideSG4SensorLayerSizeView): Disposable {
    val disposable = CompositeDisposable()

    val onUserDataRequested: Observable<SetupGuideSGC4SensorLayerSizeUiModel> =
      userComponent.getLocalUser(UUID.fromString(preferences.userLoggedIn))
        .compose(DelayedLoadingTransformer<User>())
        .map {
          if (it.result != null) {
            SetupGuideSGC4SensorLayerSizeUiModel.FetchedUser(it.result!!)
          }
          else {
            SetupGuideSGC4SensorLayerSizeUiModel.LoadingData()
          }
        }

    val updateUserObservable = uiView.onUpdateUser()
      .switchMap { user ->
        userComponent.updateUser(user)
          .subscribeOn(Schedulers.io())
          .flatMap {
            userComponent.syncUser(user.id!!)
              .map<SetupGuideSGC4SensorLayerSizeUiModel> { SetupGuideSGC4SensorLayerSizeUiModel.UserUpdated(user) }
              .onErrorReturn { SetupGuideSGC4SensorLayerSizeUiModel.UpdateFailed() }
          }
      }

    disposable.add(Observable.merge(onUserDataRequested, updateUserObservable).bind(uiView.render()))
    return disposable
  }
}