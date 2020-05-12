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
import com.coloplast.olympus.bluetooth.businesslayer.enums.Attached
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
import com.coloplast.olympus.ui.feature.guides.GuideController
import com.coloplast.olympus.ui.feature.guides.models.GuideType
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import com.coloplast.olympus.ui.feature.home.models.PatchVisible
import com.coloplast.olympus.ui.feature.home.models.SideMenuType
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

class HomeController : BaseController<ControllerHomeBinding>, HomeView, PermissionHandler, BaseDialogController.Listener {
  
  private val viewModel: HomeViewModel by inject()
  
  private val requestConnectionStateRelay = PublishRelay.create<String>()
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
  
  private var macAddress: String = ""
  
  private var currentUiState = UiState.INITIAL
  private var stateRouter: Router? = null
  
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
          //changeUiState(UiState.READY_TO_SCAN)
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
          val stateUpdate = uiModel.stateChange
          
          var updateConnectionState = false
          
          if(macAddress.isEmpty()) {
            // this is the first time coming through this controller - just pass it along
            macAddress = stateUpdate.bleDevice.macAddress
            updateConnectionState = true
          }
          else {
            // it's not the first time - and its actually another transmitter
            if (stateUpdate.bleDevice.macAddress != macAddress) {
              // is the new transmitter attached to a patch
              val attachState = stateUpdate.bleDevice.peripheral?.transmitter?.attached
              if (attachState == Attached.ATTACHED_ON_PATCH || attachState == Attached.ATTACHED_ON_BODY) {
                // yes it is - so replace current transmitter with the new one
                macAddress = stateUpdate.bleDevice.macAddress
                updateConnectionState = true
              }
              else {
                // no it's not attached, so we skip the result of this one
                return@ui
              }
            }
          }
          
          if(updateConnectionState) {
            connectionStateDisposable.clear()
            connectionStateDisposable.add(viewModel.bindConnectionState(this))
            requestConnectionStateRelay.accept(macAddress)
          }
          
          powerLevel.set(stateUpdate.bleDevice.peripheral?.powerLevel)
          isConnected.set(stateUpdate.bleDevice.isConnected())
          
          stateUpdate.bleDevice.peripheral?.transmitter?.rawRotationalAngle?.let {
            rawRotationAngle.set(it)
          }
          
          stateUpdate.bleDevice.peripheral?.patch?.let {
            currentPatch.set(it)
          }
          
          changeUiState(stateUpdate.state)
          // TODO - check overlay state too once we are ready
        }
        is HomeUiModel.RawDataPackageUpdate -> {
          // if the PackageUpdate coming from a device does not correspond the currently attached transmitter - skip it
          if (uiModel.macAddress != macAddress) {
            return@ui
          }
          
          updateAnimation.set(true)
          updateAnimation.notifyChange()
          lastCheck.set(uiModel.lastUpdate)
          
          // TODO - store rawDataPackage to send to the server at a later point
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
      .publish().refCount()
  }
  
  val patchClickedListener = object : PatchView.Listener {
    override fun onPatchClicked(patch: Patch) {
      Timber.e("Patch was clicked")
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
      else -> {
        super.onPositiveButtonClicked(requestCode)
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
      else -> {
        super.onDialogDismissed(requestCode)
      }
    }
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
          topImage = R.drawable.ic_bluetooth_black_24dp,
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
  
  override fun onSaveInstanceState(outState: Bundle) {
    outState.putBoolean("CONNECTED", isConnected.get())
    outState.putString("MAC_ADDRESS", macAddress)
    outState.putInt("UI_STATE", currentUiState.ordinal)
    
    super.onSaveInstanceState(outState)
  }
  
  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    restoreInstance(savedInstanceState)
    super.onRestoreInstanceState(savedInstanceState)
  }
  
  private fun restoreInstance(bundle: Bundle?) {
    bundle?.let {
      currentUiState = UiState.values()[bundle.getInt("UI_STATE")]
      isConnected.set(bundle.getBoolean("CONNECTED"))
      macAddress = bundle.getString("MAC_ADDRESS", "")
    }
  }
  
  override fun onDestroyView(view: View) {
    connectionStateDisposable.clear()
    super.onDestroyView(view)
  }
  
  companion object {
    const val TAG_CONTROLLER = "home_controller"
    
    private const val REQUEST_CODE_PERMISSION_LOCATION = 5001
    private const val REQUEST_CODE_PERMISSION_RATIONALE = 5002
    
    const val REQUEST_CODE_BLUETOOTH_ENABLE = 5005
    
    fun routerTransaction() = RouterTransaction.with(HomeController())
      .tag(TAG_CONTROLLER)
  }
}