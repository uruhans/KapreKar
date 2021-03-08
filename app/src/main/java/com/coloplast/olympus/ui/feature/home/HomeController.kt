package com.coloplast.olympus.ui.feature.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.ServiceDispatcher
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.bluetooth.businesslayer.enums.SensorStatus
import com.coloplast.olympus.bluetooth.businesslayer.models.Battery
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.bluetooth.model.ServiceEvent
import com.coloplast.olympus.bluetooth.model.StateChange
import com.coloplast.olympus.databinding.ControllerHomeBinding
import com.coloplast.olympus.notifications.OlympusNotificationManager
import com.coloplast.olympus.patchsplit.models.ApsAction
import com.coloplast.olympus.shared.preferences.PreferencesImpl
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.DrawerState
import com.coloplast.olympus.ui.common.conductor.ResizeChangeHandler
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.widgets.PatchView
import com.coloplast.olympus.ui.dialogs.ImageDialogController
import com.coloplast.olympus.ui.dialogs.PatchDialogController
import com.coloplast.olympus.ui.feature.about.AboutController
import com.coloplast.olympus.ui.feature.contact.ContactController
import com.coloplast.olympus.ui.feature.debug.LogsController
import com.coloplast.olympus.ui.feature.guides.GuidesFlowController
import com.coloplast.olympus.ui.feature.guides.SingleGuideFlowController
import com.coloplast.olympus.ui.feature.guides.articles.models.GuideType
import com.coloplast.olympus.ui.feature.guides.flows.tour.AppTourFlowController
import com.coloplast.olympus.ui.feature.home.models.*
import com.coloplast.olympus.ui.feature.profile.ProfileController
import com.coloplast.olympus.ui.feature.settings.SettingsFlowController
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import com.coloplast.olympus.ui.feature.transmitters.TransmittersController
import com.coloplast.olympus.ui.feature.uistates.*
import com.coloplast.olympus.utils.Utils
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject
import timber.log.Timber
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime

class HomeController : BaseController<ControllerHomeBinding>, HomeView, BaseDialogController.Listener,
  ImageDialogController.Listener {

  private val viewModel: HomeViewModel by inject()
  private val preferences: PreferencesImpl by inject()
  private val breachesOccurred: BreachesOccurred by inject()

  private val olympusNotificationManager: OlympusNotificationManager by inject()

  private val requestConnectionStateRelay = PublishRelay.create<String>()
  private val stitchOrResetRelay = PublishRelay.create<StitchOrResetObject>()

  private val connectionStateDisposable = CompositeDisposable()

  private var imageDialogController: ImageDialogController? = null

  val updateAnimation = ObservableBoolean(false)

  val battery = ObservableField<Battery>()
  val isConnected = ObservableBoolean(false)
  val lastCheck = ObservableField<LocalDateTime>()

  val currentPatch = ObservableField<Patch>()
  val rawRotationAngle = ObservableInt()
  val isPatchVisible = ObservableBoolean(false)
  val patchInErrorState = ObservableBoolean(false)
  val isLastCheckVisible = ObservableBoolean(false)

  val transmitterName = ObservableField<String>()
  val isProduction = BuildConfig.Suffix.isEmpty()

  private var currentUiState = UiState.INITIAL
  private var currentOverlayState = OverlayState.NONE

  private var stateRouter: Router? = null

  private var patchDialog: PatchDialogController? = null
  private var patchChangeTime: LocalTime? = null

  private var previousBleState: BluetoothState = BluetoothState.UNDEFINED
  private var lastResetOrStitch: Instant? = null

  constructor() : super()
  constructor(args: Bundle?) : super(args)

  override fun onActivityResumed(activity: Activity) {
    super.onActivityResumed(activity)

    // we want to force an update if we are coming from the background
    if (currentUiState != UiState.INITIAL) {
      if(currentUiState == UiState.LOCATION_SERVICES_NOT_ENABLED) {
        compositeDisposable.clear()
        compositeDisposable.add(viewModel.bind(this))
      }
      else {
        changeUiState(currentUiState, true)
      }
    }
  }

  override fun attachVariables(viewBinding: ControllerHomeBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerHomeBinding) {
    stateRouter = Conductor.attachRouter(activity!!, viewBinding.contentContainer, args)
    compositeDisposable.add(viewModel.bind(this))

    // we want to force an update if we are coming from the background
    if (currentUiState != UiState.INITIAL) {
      changeUiState(currentUiState, true)
    }
  }

  override fun render(): Function<Observable<HomeUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      if(uiModel !is HomeUiModel.AnimationReset) {
        Timber.d("uiModel: $uiModel")
      }

      when (uiModel) {
        is HomeUiModel.ConnectionStateUpdate -> {
          isConnected.set(uiModel.connectionState == BleConnectionState.CONNECTED)

          if (uiModel.connectionState != BleConnectionState.CONNECTED) {
            changeUiState(UiState.DISCONNECTED)
          }
        }
        is HomeUiModel.BluetoothIssue -> {
          when (uiModel.bluetoothState) {
            BluetoothState.BLUETOOTH_NOT_AVAILABLE -> {
            }
            BluetoothState.LOCATION_PERMISSION_NOT_GRANTED -> {
              //This is only required when scanning for transmitter
              if(!Utils.locationEnabled()) {
                changeUiState(UiState.LOCATION_SERVICES_NOT_ENABLED)
              }
            }
            BluetoothState.BLUETOOTH_NOT_ENABLED -> {
              isConnected.set(false)
              olympusNotificationManager.send("", UiState.BLUETOOTH_NOT_ENABLED)
              changeUiState(UiState.BLUETOOTH_NOT_ENABLED)
            }
            BluetoothState.LOCATION_SERVICES_NOT_ENABLED -> {
              changeUiState(UiState.LOCATION_SERVICES_NOT_ENABLED)
            }
            BluetoothState.UNDEFINED -> {

            }
            BluetoothState.READY -> {
              if (previousBleState == BluetoothState.BLUETOOTH_NOT_ENABLED || previousBleState == BluetoothState.LOCATION_SERVICES_NOT_ENABLED) {
                ServiceDispatcher.dispatchEvent(ServiceEvent.RetryConnection())
              }

              if (currentUiState == UiState.INITIAL ||
                currentUiState == UiState.BLUETOOTH_NOT_ENABLED ||
                currentUiState == UiState.LOCATION_SERVICES_NOT_ENABLED
              ) {

                //StateDispatcher.dispatchScanning()
                changeUiState(UiState.CONNECTING)
              }
            }
          }

          previousBleState = uiModel.bluetoothState
        }
        is HomeUiModel.Initial -> {
          /* no-op */
          // a placeholder for initial scanning state when first launching the app
        }
        is HomeUiModel.Scanning -> {
          if (currentUiState == UiState.INITIAL) {
            changeUiState(UiState.CONNECTING)
          }
        }
        is HomeUiModel.ScanningStopped -> {
          if (currentUiState == UiState.CONNECTING) {
            changeUiState(UiState.TRANSMITTER_NOT_FOUND)
          }
        }
        is HomeUiModel.DeviceDisconnected -> {
          isConnected.set(false)
          changeUiState(UiState.CONNECTING)
        }
        is HomeUiModel.NoTransmittersFound -> {
          if(Utils.locationEnabled()) {
            lastCheck.set(uiModel.lastUpdate)
            changeUiState(UiState.TRANSMITTERS_EMPTY)
          }
        }
        is HomeUiModel.StitchOrResetComplete -> {
          /* no-op */
//          if(!isProduction) {
//            Toast.makeText(AppSettings.applicationContext, "Reset Or Stitch sent", Toast.LENGTH_SHORT).show()
//          }
        }
        is HomeUiModel.DeviceUpdate -> {
          val stateChange = uiModel.stateChange

          transmitterName.set(stateChange.bleDevice.name)

          stateChange.bleDevice.peripheral?.battery?.let { battery.set(it) }
          isConnected.set(stateChange.bleDevice.isConnected())

          if (stateChange.state == UiState.APP_UPDATE_AVAILABLE
            || stateChange.state == UiState.FIRMWARE_AVAILABLE
          ) {
            changeUiState(stateChange.state)
            return@ui
          }

          stateChange.bleDevice.peripheral?.transmitter?.rawRotationalAngle?.let {
            rawRotationAngle.set(it)
          }

          stateChange.bleDevice.peripheral?.patch?.let {
            currentPatch.set(it)
          }

          var stateToChangeTo = stateChange.state

          // Special case for Wet Mount
          // if we get a wet mount or outer leak within 4 minutes (240 seconds)
          // we override the ui state to wet mount
          val timeBetween = LocalTime.now().minusNanos(patchChangeTime?.toNanoOfDay() ?: 0)

          if (timeBetween.toSecondOfDay() <= 320) {
            val patch = stateChange.bleDevice.peripheral?.patch
            if (patch != null) {
              if (patch.isOuterSensorWet) {
                stateToChangeTo = UiState.WET_MOUNT_G
              }
              else if (patch.isInnerSensorsWet) {
                stateToChangeTo = UiState.WET_MOUNT_X
              }
            }
            // clear the variable so we dont hit this wet mount again the next time
            patchChangeTime = null
          }
          else {
            // if we are passed 4 minutes we dont need this variable any more
            patchChangeTime = null
          }

          if (stateChange.overlayState == OverlayState.BLE_CONNECTION_LOST) {
            isConnected.set(false)
          }
          else if(stateChange.overlayState == OverlayState.NONE && imageDialogController?.requestCode == REQUEST_CODE_CONNECTION_LOST) {
            imageDialogController?.dismissDialog(false)
            imageDialogController = null
          }

          // this was added to prevent "blinking" ui when "switching" between OK_A and SOMETHING_ELSE
          if(stateToChangeTo == UiState.SOMETHING_ELSE) {
            Timber.d("Overriding [$stateToChangeTo] => [OK_A]")
            stateToChangeTo = UiState.OK_A
          }

          changeUiState(stateToChangeTo)
          changeOverlayState(stateChange.overlayState)

          determineApsAction(stateChange)

          if (stateChange.apsAction == ApsAction.Split) {
            breachesOccurred.clear()
          }
          else {
            breachesOccurred.determineBreach(stateChange)
          }
        }
        is HomeUiModel.RawDataPackageUpdate -> {
          lastCheck.set(uiModel.lastUpdate)
        }
        is HomeUiModel.AnimationReset -> {
          updateAnimation.set(true)
          updateAnimation.notifyChange()
        }
      }
    }
  }

  private fun changeUiState(newState: UiState, forceUpdate: Boolean = false) {
    var removeBleDisconnectOverlay = true

    if (currentUiState == newState && !forceUpdate) {
      Timber.d("Same state was hit [${newState}] - Skipping")
    }
    else {
      Timber.d("State is [${newState}]")

      val routerTransaction = when (newState) {
        UiState.FIRMWARE_AVAILABLE -> {
          FirmwareAvailableController.routerTransaction()
        }
        UiState.APP_UPDATE_AVAILABLE -> {
          AppUpdateAvailableController.routerTransaction()
        }
        UiState.SOMETHING_ELSE, // TODO - something else might need to be removed
        UiState.OK_A -> {
          OkController.routerTransaction()
        }
        UiState.INNER_BREACH_B -> {
          InnerBreachController.routerTransaction()
        }
        UiState.OUTER_BREACH_C -> {
          OuterBreachController.routerTransaction()
        }
        UiState.INNER_BLIND_D -> {
          InnerBlindController.routerTransaction()
        }
        UiState.TOTAL_BLIND_E -> {
          TotalBlindController.routerTransaction()
        }
        UiState.PAY_ATTENTION_F -> {
          PayAttentionController.routerTransaction()
        }
        UiState.WET_MOUNT_G -> {
          WetMountController.routerTransaction(R.string.status_wet_mount_title,  R.string.status_wet_mount)
        }
        UiState.CUTGUARD_BROKEN_I -> {
          CutGuardController.routerTransaction()
        }
        UiState.WATER_INGRESS_DETECTION_J -> {
          WaterIngressDetectionController.routerTransaction()
        }
        UiState.CUTGUARD_DIRT_IN_TRANSMITTER_K -> {
          DirtInTransmitterController.routerTransaction()
        }
        UiState.INNER_BLIND_LEAK_L -> {
          InnerBlindLeakController.routerTransaction()
        }
        UiState.NO_BATTERY_R -> {
          NoBatteryController.routerTransaction()
        }
        UiState.TRANSMITTER_DETACHED_V -> {
          TransmitterDetachedController.routerTransaction()
        }
        UiState.TRANSMITTER_BATTERY_CHARGING_W -> {
          TransmitterChargingController.routerTransaction()
        }
        UiState.WET_MOUNT_X -> {
          WetMountController.routerTransaction(R.string.status_inner_blind_mount_title, R.string.status_inner_blind_mount_description)
        }
        UiState.TRANSMITTERS_EMPTY -> {
          NoTransmittersController.routerTransaction()
        }
        UiState.BLUETOOTH_NOT_ENABLED -> {
          removeBleDisconnectOverlay = false
          BluetoothNotEnabledController.routerTransaction()
        }
        UiState.LOCATION_SERVICES_NOT_ENABLED -> {
          LocationNotEnabledController.routerTransaction()
        }
        UiState.MULTIPLE_TRANSMITTERS -> {
          MultipleTransmittersController.routerTransaction()
        }
        UiState.TRANSMITTER_NOT_FOUND -> {
          TransmitterNotFoundController.routerTransaction()
        }
        UiState.DISCONNECTED -> {
          removeBleDisconnectOverlay = false
          ConnectingController.routerTransaction()
        }
        UiState.INITIAL,
        UiState.CONNECTING -> {
          ConnectingController.routerTransaction()
        }
        UiState.SCANNING -> {
          ScanningController.routerTransaction()
        }
        else -> {
          null
        }
      }

      routerTransaction?.let {
        if (it.controller() is LastCheckVisible) {
          isLastCheckVisible.set((it.controller() as LastCheckVisible).lastCheckVisible())
        }

        if (it.controller() is PatchVisible) {
          it.popChangeHandler(ResizeChangeHandler())
            .pushChangeHandler(ResizeChangeHandler())

          patchInErrorState.set((it.controller() as PatchVisible).patchInError())
          isPatchVisible.set((it.controller() as PatchVisible).patchVisible())
        }
        else {
          it.popChangeHandler(FadeChangeHandler())
            .pushChangeHandler(FadeChangeHandler())

          isPatchVisible.set(false)
          isLastCheckVisible.set(false)
        }

        if (stateRouter?.hasRootController() == true) {
          stateRouter?.replaceTopController(it)
        }
        else {
          stateRouter?.setRoot(it)
        }
      }

      if (removeBleDisconnectOverlay && imageDialogController?.requestCode == REQUEST_CODE_CONNECTION_LOST) {
        onCloseClicked(REQUEST_CODE_CONNECTION_LOST)
      }
    }

    if (newState != UiState.NO_CHANGE) {
      currentUiState = newState
    }
  }

  private fun changeOverlayState(overlayState: OverlayState) {
    if (currentOverlayState == overlayState) {
      return
    }

    when (overlayState) {
      OverlayState.BATTERY_LOW -> {
        imageDialogController = ImageDialogController.create(
          this,
          REQUEST_CODE_BATTERY_LOW,
          R.string.overlay_battery_low_title,
          R.string.status_no_con_battery,
          R.string.guide_charge,
          topImage = R.drawable.ic_battery_low_24dp,
          verticalButtonLayout = true,
          dismissAllowed = true
        )
      }
      OverlayState.BLE_CONNECTION_LOST -> {
        imageDialogController = ImageDialogController.create(
          this,
          REQUEST_CODE_CONNECTION_LOST,
          R.string.overlay_ble_disconnected_title,
          R.string.overlay_ble_disconnected,
          R.string.guide_ble_connection,
          topImage = R.drawable.ic_bluetooth,
          tintColor = R.color.darkGrey03,
          verticalButtonLayout = true,
          dismissAllowed = true
        )
      }
    }

    imageDialogController?.show()
    currentOverlayState = overlayState
  }

  override fun requestConnectionState(): Observable<String> {
    return requestConnectionStateRelay
      .publish()
      .refCount()
  }

  override fun requestStitching(): Observable<StitchOrResetObject> {
    return stitchOrResetRelay.publish().refCount()
  }

  val patchClickedListener = object : PatchView.Listener {
    override fun onPatchClicked() {
      val controllerAsPatchVisible = stateRouter?.backstack?.first()?.controller() as? PatchVisible

      patchDialog = PatchDialogController.create(
        this@HomeController,
        REQUEST_CODE_PATCH_DETAILS,
        controllerAsPatchVisible?.patchInError() ?: false,
        dismissAllowed = false
      )

      patchDialog?.show()
    }
  }

  private fun determineApsAction(stateChange: StateChange) {
    if (stateChange.apsAction == ApsAction.Nothing) {
      return
    }

    var timeSinceLast = 100L
    if(lastResetOrStitch != null) {
      timeSinceLast = Instant.now().epochSecond - lastResetOrStitch!!.epochSecond
    }

    if(lastResetOrStitch == null || timeSinceLast > 12) {
      Timber.wtf("ApsAction: [${stateChange.apsAction}] on transmitter ${transmitterName.get()}")

      if(stateChange.apsAction == ApsAction.Split) {
        Timber.wtf("OLD patchId: [${preferences.getCurrentPatchId()}]")
        preferences.incrementPatchId()
        patchChangeTime = LocalTime.now()
        Timber.wtf("NEW patchId: [${preferences.getCurrentPatchId()}]")
      }

      stateChange.resetOrStitchCandidate?.let {
        lastResetOrStitch = Instant.now()
        stitchOrResetRelay.accept(StitchOrResetObject(stateChange.getMacAddress(), it))
      }
    }
  }

  override fun onPositiveButtonClicked(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_BATTERY_LOW,
      REQUEST_CODE_CONNECTION_LOST -> {
        imageDialogController?.dismissDialog(withCallback = true)
      }
    }
  }

  override fun onDialogDismissed(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_CONNECTION_LOST -> {
        router.pushController(
          SingleGuideFlowController.routerTransaction(GuideType.CONNECTION)
            .pushChangeHandler(VerticalChangeHandler())
            .popChangeHandler(VerticalChangeHandler())
        )
      }
      REQUEST_CODE_BATTERY_LOW -> {
        router.pushController(
          SingleGuideFlowController.routerTransaction(GuideType.CHARGING)
            .pushChangeHandler(VerticalChangeHandler())
            .popChangeHandler(VerticalChangeHandler())
        )
      }
    }

    imageDialogController = null
  }

  override fun onCloseClicked(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_CONNECTION_LOST,
      REQUEST_CODE_BATTERY_LOW -> {
        imageDialogController?.dismissDialog(false)
        imageDialogController = null
      }
    }
  }

  fun onMenuClicked() {
    if (getBinding().drawerLayout.isDrawerOpen(GravityCompat.END)) {
      changeDrawerState(DrawerState.CLOSED)
    }
    else {
      changeDrawerState(DrawerState.OPENED)
    }
  }

  fun changeDrawerState(drawerState: DrawerState) {
    when (drawerState) {
      DrawerState.OPENED -> getBinding().drawerLayout.openDrawer(GravityCompat.END, true)
      DrawerState.CLOSED -> getBinding().drawerLayout.closeDrawer(GravityCompat.END, true)
    }
  }

  fun onSideMenuClicked(menuType: SideMenuType) {
    when (menuType) {
      SideMenuType.STATUS -> {
        changeDrawerState(DrawerState.CLOSED)
      }
      SideMenuType.GUIDES -> {
        router.pushController(
          GuidesFlowController.getRouterTransaction()
            .pushChangeHandler(VerticalChangeHandler())
            .popChangeHandler(VerticalChangeHandler())
        )
      }
      SideMenuType.TRANSMITTERS -> {
        router.pushController(
          TransmittersController.routerTransaction()
            .pushChangeHandler(VerticalChangeHandler())
            .popChangeHandler(VerticalChangeHandler())
        )
      }
      SideMenuType.SETTINGS -> {
        router.pushController(
          SettingsFlowController.routerTransaction()
            .pushChangeHandler(VerticalChangeHandler())
            .popChangeHandler(VerticalChangeHandler())
        )
      }
      SideMenuType.VERSION -> {
        getContentRouter().pushController(
          LogsController.routerTransaction()
            .pushChangeHandler(VerticalChangeHandler())
            .popChangeHandler(VerticalChangeHandler())
        )
      }
      SideMenuType.PROFILE -> {
        router.pushController(
          ProfileController.routerTransaction()
            .pushChangeHandler(VerticalChangeHandler())
            .popChangeHandler(VerticalChangeHandler())
        )
      }
      SideMenuType.CONTACT -> {
        router.pushController(
          ContactController.routerTransaction()
            .pushChangeHandler(VerticalChangeHandler())
            .popChangeHandler(VerticalChangeHandler())
        )
      }
      SideMenuType.ABOUT -> {
        router.pushController(
          AboutController.routerTransaction()
            .pushChangeHandler(VerticalChangeHandler())
            .popChangeHandler(VerticalChangeHandler())
        )
      }
    }
  }

  fun shouldShowAppTourButton(): Boolean {
    return preferences.shouldShowAppTour()
  }

  fun startAppTour() {
    getContentRouter().pushController(
      AppTourFlowController.routerTransaction()
        .pushChangeHandler(HorizontalChangeHandler())
        .popChangeHandler(HorizontalChangeHandler())
    )
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      REQUEST_CODE_BLUETOOTH_ENABLE -> {
        if (resultCode == Activity.RESULT_CANCELED) {
          Timber.d("enable it, dude")
        }
      }
      else -> super.onActivityResult(requestCode, resultCode, data)
    }
  }

  override fun getLayout(): Int {
    return R.layout.controller_home
  }

  override fun handleBack(): Boolean {
    return if (getBinding().drawerLayout.isDrawerOpen(GravityCompat.END)) {
      onMenuClicked()
      true
    }
    else {
      super.handleBack()
    }
  }

  override fun onDestroyView(view: View) {
    connectionStateDisposable.clear()
    super.onDestroyView(view)
  }

  companion object {
    const val TAG_CONTROLLER = "home_controller"

    private const val REQUEST_CODE_BATTERY_LOW = 6005
    private const val REQUEST_CODE_CONNECTION_LOST = 6010
    private const val REQUEST_CODE_PATCH_DETAILS = 7001

    const val REQUEST_CODE_BLUETOOTH_ENABLE = 5005

    fun routerTransaction() = RouterTransaction.with(HomeController())
      .tag(TAG_CONTROLLER)
  }
}