package com.coloplast.olympus.ui.feature.home

import com.artemzin.rxui.kotlin.bind
import com.coloplast.olympus.Me
import com.coloplast.olympus.bluetooth.StateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.BluetoothManager
import com.coloplast.olympus.bluetooth.businesslayer.ServiceStateDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.businesslayer.interfaces.AppStateComponent
import com.coloplast.olympus.businesslayer.interfaces.TransmitterComponent
import com.coloplast.olympus.businesslayer.interfaces.UserComponent
import com.coloplast.olympus.shared.models.CurrentUser
import com.coloplast.olympus.shared.models.DataResult
import com.coloplast.olympus.ui.feature.home.models.BaseHomeViewModel
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class HomeViewModel(
  private val me: Me,
  private val bluetoothManager: BluetoothManager,
  private val transmitterComponent: TransmitterComponent,
  private val userComponent: UserComponent,
  private val appStateComponent: AppStateComponent,
  private val serviceStateDispatcher: ServiceStateDispatcher
) : BaseHomeViewModel() {

  override fun bind(uiView: HomeView): Disposable {
    val disposable = CompositeDisposable()

    val currentBluetoothStateObservable: Observable<BluetoothState> = bluetoothManager.bluetoothStateObservable()
    val bluetoothStateObservable: Observable<HomeUiModel> = currentBluetoothStateObservable
      .flatMap<HomeUiModel> {
        return@flatMap currentBluetoothStateObservable
          .flatMap {
            if (it == BluetoothState.LOCATION_PERMISSION_NOT_GRANTED || it == BluetoothState.BLUETOOTH_NOT_ENABLED) {
              dbInsertAppState(UiState.BLUETOOTH_NOT_ENABLED)
                .toSingle { HomeUiModel.BluetoothIssue(BluetoothState.BLUETOOTH_NOT_ENABLED) }
                .toObservable()
            }
            else {
              Observable.just(HomeUiModel.BluetoothIssue(it))
            }
          }
      }
      .onErrorReturn {
        Timber.e(it)
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
      .flatMap { uiModel ->
        if (uiModel is HomeUiModel.DeviceUpdate) {
          val state = if (uiModel.stateChange.bleDevice.isConnected().not()) {
            UiState.DISCONNECTED
          }
          else {
            uiModel.stateChange.state
          }
          return@flatMap dbInsertAppState(state)
            .toSingle { uiModel }
            .toObservable()
        }
        return@flatMap Observable.just(uiModel)
      }

    val animationResetObservable = Observable.interval(10, 10, TimeUnit.SECONDS)
      .map { HomeUiModel.AnimationReset() }

    val rawPackageUpdateObservable = StateDispatcher.rawPackageUpdateObservable()
      .map { HomeUiModel.RawDataPackageUpdate(it) }

    val animationAndRawPackageObservable = Observable.merge(
      animationResetObservable,
      rawPackageUpdateObservable
    )

    val stitchTransmitterObservable = uiView.requestStitching()
      .doOnNext { obj -> Timber.d("ResetOrStitch: [${obj.dataPackage.joinToString { it.toInt().toString() }}]") }
      .switchMap { stitch ->
        bluetoothManager.stitchOrResetTransmitter(stitch.macAddress, stitch.dataPackage)
          .toSingle<HomeUiModel> { HomeUiModel.StitchOrResetComplete() }
          .toObservable()
      }

    val transmittersObservable = transmitterComponent.getTransmittersAsSingle()
      .subscribeOn(Schedulers.io())
      .toObservable()
      .filter { list -> list.isEmpty() }

    val synchronizeDataObservable = me.currentUserObservable()
      .subscribeOn(Schedulers.io())
      .ofType(CurrentUser.LoggedIn::class.java)
      .map { it.user.id }
      .flatMap { userId ->
        Observable.concat(
          transmitterComponent.syncTransmitters(userId),
          userComponent.syncUser(userId)
        )
      }
      .ignoreElements()

    val noTransmittersObservable =
      currentBluetoothStateObservable
        .filter { it != BluetoothState.UNDEFINED }
        .flatMap {
          if (it == BluetoothState.LOCATION_SERVICES_NOT_ENABLED || it == BluetoothState.BLUETOOTH_NOT_ENABLED) {
            return@flatMap Observable.just(HomeUiModel.BluetoothIssue(it))
          }
          else {
            return@flatMap transmittersObservable
              .flatMap {
                dbInsertAppState(UiState.TRANSMITTERS_EMPTY)
                  .toSingle { HomeUiModel.NoTransmittersFound() }
                  .toObservable()
              }
          }
        }


    val requestResetOverlayObservable = uiView.requestResetOverlay()
      .switchMapMaybe {
        StateDispatcher.activeTransmitterStateChangeObservable()
          .firstOrError()
          .filter { it is DataResult.Success }
          .map { it as DataResult.Success }
          .map { it.data }
          .map {
            if(it.overlayState != OverlayState.NONE) {
              Timber.wtf("OverlayState reset")
              val newState = StateChange(it.state, OverlayState.NONE, it.bleDevice, it.apsAction, it.timestamp, it.resetOrStitchCandidate, it.isBatteryUpdate)
              StateDispatcher.dispatchStateForActiveTransmitter(newState, false)
            }
          }
      }

    disposable.add(requestResetOverlayObservable.subscribe())
    disposable.add(synchronizeDataObservable.subscribe())
    disposable.add(Observable.merge(stateUpdate, bluetoothStateObservable, animationAndRawPackageObservable, noTransmittersObservable).bind(uiView.render()))
    disposable.add(stitchTransmitterObservable.bind(uiView.render()))

    return disposable
  }

  private fun dbInsertAppState(uiState: UiState): Completable {
    return appStateComponent.insertAppState(uiState.toLetter())
      .subscribeOn(Schedulers.io())
      .onErrorComplete()
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


