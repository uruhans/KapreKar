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
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.enums.BleConnectionState
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.databinding.ControllerHomeBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.DrawerState
import com.coloplast.olympus.ui.common.PermissionHandler
import com.coloplast.olympus.ui.common.conductor.ResizeChangeHandler
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.common.widgets.PatchView
import com.coloplast.olympus.ui.dialogs.ImageDialogController
import com.coloplast.olympus.ui.dialogs.PatchDialogController
import com.coloplast.olympus.ui.feature.TransmitterManager
import com.coloplast.olympus.ui.feature.guides.GuideController
import com.coloplast.olympus.ui.feature.guides.models.GuideType
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import com.coloplast.olympus.ui.feature.home.models.PatchVisible
import com.coloplast.olympus.ui.feature.home.models.SideMenuType
import com.coloplast.olympus.ui.feature.home.models.Stitch
import com.coloplast.olympus.ui.feature.statemachine.models.OverlayState
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import com.coloplast.olympus.ui.feature.transmitters.TransmittersController
import com.coloplast.olympus.ui.feature.uistates.*
import com.coloplast.olympus.utils.OSUtils
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.core.inject
import timber.log.Timber
import java.time.LocalDateTime

class HomeController : BaseController<ControllerHomeBinding>, HomeView, PermissionHandler, BaseDialogController.Listener, ImageDialogController.Listener {
  
  private val viewModel: HomeViewModel by inject()
  private val transmitterManager: TransmitterManager by inject()
  private val breachesOccurred: BreachesOccurred by inject()

  private val requestConnectionStateRelay = PublishRelay.create<String>()
  private val stitchRelay = PublishRelay.create<Stitch>()
  private val resetRelay = PublishRelay.create<String>() // macAddress

  private val connectionStateDisposable = CompositeDisposable()

  private var dialogController: DialogController? = null
  private var imageDialogController: ImageDialogController? = null
  
  val updateAnimation = ObservableBoolean(false)
  
  val powerLevel = ObservableField<PowerLevel>()
  val isConnected = ObservableBoolean(false)
  val lastCheck = ObservableField<LocalDateTime>()
  
  val currentPatch = ObservableField<Patch>()
  val rawRotationAngle = ObservableInt()
  val isPatchVisible = ObservableBoolean(false)
  val patchInErrorState = ObservableBoolean(false)

  private var currentUiState = UiState.INITIAL
  private var stateRouter: Router? = null

  private var patchDialog: PatchDialogController? = null

  constructor() : super()
  constructor(args: Bundle?) : super(args)
  
  override fun attachVariables(viewBinding: ControllerHomeBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerHomeBinding) {
    stateRouter = Conductor.attachRouter(activity!!, viewBinding.contentContainer, args)
    
    compositeDisposable.add(viewModel.bind(this))
    
    if (currentUiState != UiState.INITIAL) {
      changeUiState(currentUiState, true)
    }
  }
  
  override fun render(): Function<Observable<HomeUiModel>, Disposable> {
    return RxUi.ui { uiModel ->

      Timber.d("uiModel: $uiModel")
      
      when (uiModel) {
        is HomeUiModel.ConnectionStateUpdate -> {
          isConnected.set(uiModel.connectionState == BleConnectionState.CONNECTED)
          
          if(uiModel.connectionState != BleConnectionState.CONNECTED) {
            changeUiState(UiState.DISCONNECTED)
          }
        }
        is HomeUiModel.BluetoothIssue -> {
          when (uiModel.bluetoothState) {
            BluetoothState.BLUETOOTH_NOT_AVAILABLE -> {
            }
            BluetoothState.LOCATION_PERMISSION_NOT_GRANTED -> {
              showPermissionDialog()
            }
            BluetoothState.BLUETOOTH_NOT_ENABLED -> {
              if (currentUiState == UiState.INITIAL || currentUiState == UiState.READY_TO_SCAN) {
                changeUiState(UiState.BLUETOOTH_NOT_ENABLED)
              }
            }
            BluetoothState.LOCATION_SERVICES_NOT_ENABLED -> {
            
            }
            BluetoothState.UNDEFINED -> {
            
            }
            BluetoothState.READY -> {
              if (currentUiState == UiState.INITIAL || currentUiState == UiState.BLUETOOTH_NOT_ENABLED) {
                changeUiState(UiState.READY_TO_SCAN)
              }
            }
          }
        }
        is HomeUiModel.DeviceUpdate -> {
          val updatedStateChange = transmitterManager.determineCurrentlyActiveTransmitterAndReturnStateChangeFromIt(uiModel.stateChange)
          val stateChange = transmitterManager.determineTransmitterPatchQuestioning(updatedStateChange)

          powerLevel.set(stateChange.bleDevice.peripheral?.powerLevel)
          isConnected.set(stateChange.bleDevice.isConnected())

          breachesOccurred.add(stateChange)

          stateChange.bleDevice.peripheral?.transmitter?.rawRotationalAngle?.let {
            rawRotationAngle.set(it)
          }
          
          stateChange.bleDevice.peripheral?.patch?.let {
            currentPatch.set(it)
          }

          changeUiState(stateChange.state)

          if(stateChange.overlayState == OverlayState.QUESTION) {
            imageDialogController = ImageDialogController.create(
              this,
              REQUEST_CODE_TRANSMITTERS_CHANGED,
              R.string.overlay_leak_attactment_title,
              0,
              R.string.overlay_leak_attactment_action_new,
              R.string.overlay_leak_attactment_action_old,
              R.drawable.ic_info_circle,
              verticalButtonLayout = true,
              dismissAllowed = false
            )

            imageDialogController?.show()
          }
        }
        is HomeUiModel.RawDataPackageUpdate -> {
          updateAnimation.set(true)
          updateAnimation.notifyChange()
          lastCheck.set(uiModel.lastUpdate)
        }
      }
    }
  }
  
  private fun changeUiState(newState: UiState, forceUpdate: Boolean = false) {
    if (currentUiState == newState && !forceUpdate) {
      Timber.d("Same state was hit [${newState}] - Skipping")
    }
    else {
      val routerTransaction = when (newState) {
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
        UiState.TOTAL_BLIND_E -> {
          TotalBlindController.routerTransaction()
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
        UiState.TRANSMITTER_DETACHED_V -> {
          TransmitterDetachedController.routerTransaction()
        }
        UiState.TRANSMITTER_BATTERY_CHARGING_W -> {
          TransmitterChargingController.routerTransaction()
        }
        UiState.BLUETOOTH_NOT_ENABLED -> {
          BluetoothNotEnabledController.routerTransaction()
        }
        UiState.MULTIPLE_TRANSMITTERS -> {
          MultipleTransmittersController.routerTransaction()
        }
        UiState.DISCONNECTED -> {
          ReadyToScanController.routerTransaction()
        }
        UiState.INITIAL -> {
          UnknownController.routerTransaction()
        }
        UiState.READY_TO_SCAN -> {
          ReadyToScanController.routerTransaction()
        }
        UiState.SCANNING -> {
          ScanningController.routerTransaction()
        }
        else -> {
          null
        }
      }
      
      routerTransaction?.let {
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
        }
        
        if (stateRouter?.hasRootController() == true) {
          stateRouter?.replaceTopController(it)
        }
        else {
          stateRouter?.setRoot(it)
        }
      }
    }
    
    if (newState != UiState.NO_CHANGE) {
      currentUiState = newState
    }
  }
  
  override fun requestConnectionState(): Observable<String> {
    return requestConnectionStateRelay
      .publish()
      .refCount()
  }

  override fun requestStitching(): Observable<Stitch> {
    return stitchRelay.publish().refCount()
  }

  override fun requestReset(): Observable<String> {
    return resetRelay.publish().refCount()
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
  
  override fun onPositiveButtonClicked(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_PERMISSION_LOCATION -> {
        dialogController?.dismissDialog(withCallback = true)
      }
      REQUEST_CODE_PERMISSION_RATIONALE -> {
        imageDialogController?.dismissDialog(withCallback = true)
      }
      REQUEST_CODE_TRANSMITTERS_CHANGED -> {
        breachesOccurred.clear()
        resetRelay.accept(transmitterManager.activeTransmitterMacAddress())
        imageDialogController?.dismissDialog(false)
      }
    }
  }

  override fun onNegativeButtonClicked(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_TRANSMITTERS_CHANGED -> {
        transmitterManager.getStitchBytesFromOtherTransmitter().let {
          stitchRelay.accept(it)
        }
        imageDialogController?.dismissDialog(false)
      }
    }
  }

  override fun onDialogDismissed(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_PERMISSION_LOCATION -> {
        requestPermission(this, REQUEST_CODE_PERMISSION_LOCATION, OSUtils.bleLocationPermission())
      }
      REQUEST_CODE_PERMISSION_RATIONALE -> {
        requestPermissionAgain(this, REQUEST_CODE_PERMISSION_LOCATION, OSUtils.bleLocationPermission())
      }
    }
  }

  override fun onCloseClicked(requestCode: Int) {

  }

  override fun showPermissionRationale(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_PERMISSION_LOCATION -> {
        imageDialogController = ImageDialogController.create(
          this,
          REQUEST_CODE_PERMISSION_RATIONALE,
          R.string.dialog_permission_rationale_location_title,
          R.string.dialog_permission_rationale_location_message,
          R.string.dialog_permission_rationale_location_ask_again,
          topImage = R.drawable.ic_bluetooth,
          verticalButtonLayout = true,
          dismissAllowed = true
        )
        
        imageDialogController?.show()
      }
    }
  }
  
  override fun onPermissionGranted(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_PERMISSION_LOCATION -> {
        Timber.d("Permission was granted")
      }
    }
  }
  
  override fun onPermissionDenied(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_PERMISSION_LOCATION -> {
        showPermissionDialog()
      }
    }
  }
  
  private fun showPermissionDialog() {
    dialogController = DialogController.create(
      this,
      REQUEST_CODE_PERMISSION_LOCATION,
      R.string.dialog_permission_location_title,
      R.string.dialog_permission_location_message,
      R.string.dialog_permission_ask,
      dismissAllowed = false
    )
    
    dialogController?.show()
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
    when(drawerState) {
      DrawerState.OPENED -> getBinding().drawerLayout.openDrawer(GravityCompat.END, true)
      DrawerState.CLOSED -> getBinding().drawerLayout.closeDrawer(GravityCompat.END, true)
    }
  }
  
  fun onSideMenuClicked(menuType: SideMenuType) {
    when (menuType) {
      SideMenuType.STATUS -> {
        router.pushController(
          GuideController.routerTransaction(GuideType.GET_STARTED)
            .pushChangeHandler(VerticalChangeHandler())
            .popChangeHandler(VerticalChangeHandler())
        )
      }
      SideMenuType.GUIDES -> {
        router.pushController(
          GuideController.routerTransaction(GuideType.CONNECTION)
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
      SideMenuType.SETTINGS,
      SideMenuType.PROFILE,
      SideMenuType.ABOUT,
      SideMenuType.CONTACT -> { /* TODO - fix this */ }
    }
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

  override fun onDestroyView(view: View) {
    connectionStateDisposable.clear()
    super.onDestroyView(view)
  }
  
  companion object {
    const val TAG_CONTROLLER = "home_controller"
    
    private const val REQUEST_CODE_PERMISSION_LOCATION = 5001
    private const val REQUEST_CODE_PERMISSION_RATIONALE = 5002
    private const val REQUEST_CODE_TRANSMITTERS_CHANGED = 6001
    private const val REQUEST_CODE_PATCH_DETAILS = 7001

    const val REQUEST_CODE_BLUETOOTH_ENABLE = 5005
    
    fun routerTransaction() = RouterTransaction.with(HomeController())
      .tag(TAG_CONTROLLER)
  }
}