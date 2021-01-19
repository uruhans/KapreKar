package com.coloplast.olympus.ui.feature.home

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.bluetooth.StateDbUpdateDispatcher
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.model.BluetoothServiceModel
import com.coloplast.olympus.businesslayer.interfaces.AppStateComponent
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.feature.home.models.BaseHomeViewModel
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class HomeViewModel(
  private val bluetoothManager: BluetoothManager,
  private val transmitterComponent: TransmitterComponent,
  private val userComponent: UserComponent,
  private val preferences: Preferences,
  private val appStateComponent: AppStateComponent
) : BaseHomeViewModel() {

  override fun bind(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()

    val currentBluetoothStateObservable: Observable<BluetoothState> = bluetoothManager.bluetoothStateObservable()
    val bluetoothStateObservable: Observable<HomeUiModel> = currentBluetoothStateObservable
      .map<HomeUiModel> { HomeUiModel.BluetoothIssue(it) }
      .onErrorReturn {
        Timber.e(it)
        StateDbUpdateDispatcher.dispatchUiStateChange(UiState.uiStateToUiStateLetter(UiState.DISCONNECTED))
        HomeUiModel.ConnectionStateUpdate(BleConnectionState.DISCONNECTED)
      }

    val stateUpdate = StateDispatcher.activeTransmitterStateChangeObservable()
      .filter { it is DataResult.Success }
      .map { it as DataResult.Success }
      .map { it.data }
      .map {
        if (it.state == UiState.TRANSMITTER_DELETED) {
          HomeUiModel.DeviceDisconnected()
        }
        else {
          HomeUiModel.DeviceUpdate(it)
        }
      }

    val animationResetObservable = Observable.interval(1, 10, TimeUnit.SECONDS)
      .map { HomeUiModel.AnimationReset() }

    val stitchTransmitterObservable = uiView.requestStitching()
      .switchMap { stitch ->
        bluetoothManager.stitchOrResetTransmitter(stitch.macAddress, stitch.dataPackage)
          .toSingle<HomeUiModel> { HomeUiModel.StitchOrResetComplete() }
          .toObservable()
      }

    val transmittersObservable = transmitterComponent.getTransmittersAsSingle()
      .subscribeOn(Schedulers.io())
      .toObservable()
      .filter { list -> list.isEmpty() }

    val userId = UUID.fromString(preferences.userLoggedIn)

    val syncTransmittersObservable = transmitterComponent.syncTransmitters(userId)
      .subscribeOn(Schedulers.io())
      .doOnNext { Timber.d("Sync Transmitters [$it]") }

    val noTransmittersObservable =
      currentBluetoothStateObservable
        .filter { it != BluetoothState.UNDEFINED }
        .flatMap {
          if (it == BluetoothState.LOCATION_SERVICES_NOT_ENABLED || it == BluetoothState.BLUETOOTH_NOT_ENABLED) {
            if (it == BluetoothState.BLUETOOTH_NOT_ENABLED) {
               StateDbUpdateDispatcher.dispatchUiStateChange(UiState.uiStateToUiStateLetter(UiState.BLUETOOTH_NOT_ENABLED))
            }
            return@flatMap Observable.just(HomeUiModel.BluetoothIssue(it))
          }
          else {
            StateDbUpdateDispatcher.dispatchUiStateChange(UiState.uiStateToUiStateLetter(UiState.TRANSMITTER_NOT_FOUND))
            return@flatMap transmittersObservable
              .map { HomeUiModel.NoTransmittersFound() }
          }
        }

    val appStateDBObservable = StateDbUpdateDispatcher.uiStateChangeObservable()
      .switchMap { uiStateLetter ->
        appStateComponent.insertAppState(uiStateLetter, AppSettings.appInBackground)
          .subscribeOn(Schedulers.io())
          .doOnError { throwable -> Timber.e(throwable) }
          .toSingle<BluetoothServiceModel> { BluetoothServiceModel.AppStateInsertComplete() }
          .toObservable()
      }

    val syncUserObservable = userComponent.syncUser(userId)
      .subscribeOn(Schedulers.io())
      .doOnNext { Timber.d("Sync User [$it]") }

    disposable.add(syncTransmittersObservable.subscribe())
    disposable.add(syncUserObservable.subscribe())
    disposable.add(appStateDBObservable.subscribe())

    disposable.add(Observable.merge(stateUpdate, bluetoothStateObservable, animationResetObservable, noTransmittersObservable).bind(uiView.render()))
    disposable.add(stitchTransmitterObservable.bind(uiView.render()))

    return disposable
  }

  override fun bindConnectionState(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()

    val connectionStateObservable = uiView.requestConnectionState()
      .switchMap { macAddress ->
        bluetoothManager.bleConnectionStateObservable(macAddress)
          .map<HomeUiModel> { HomeUiModel.ConnectionStateUpdate(it) }
      }

    disposable.add(connectionStateObservable.bind(uiView.render()))
    return disposable
  }
}