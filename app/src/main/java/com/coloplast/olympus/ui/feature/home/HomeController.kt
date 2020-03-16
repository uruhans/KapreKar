package com.coloplast.olympus.ui.feature.home

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.DeviceConnection
import com.coloplast.olympus.bluetooth.businesslayer.enums.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.databinding.ControllerHomeBinding
import com.coloplast.olympus.notifications.OlympusNotificationManager
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.PermissionHandler
import com.coloplast.olympus.ui.common.conductor.ResizeChangeHandler
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.common.widgets.PatchView
import com.coloplast.olympus.ui.dialogs.ImageDialogController
import com.coloplast.olympus.ui.feature.guides.getstarted.GetStartedGuideController
import com.coloplast.olympus.ui.feature.home.models.ConnectedBleDevice
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import com.coloplast.olympus.ui.feature.home.models.PatchVisible
import com.coloplast.olympus.ui.feature.home.models.ScanResultItem
import com.coloplast.olympus.ui.feature.statemachine.models.UiState
import com.coloplast.olympus.ui.feature.uistates.*
import com.coloplast.olympus.utils.OSUtils
import com.google.android.material.navigation.NavigationView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.koin.core.inject
import org.threeten.bp.LocalDateTime
import timber.log.Timber

class HomeController : BaseController<ControllerHomeBinding>, HomeView, PermissionHandler, BaseDialogController.Listener, ScanResultItem.Listener {
  
  val items = ObservableArrayList<ScanResultItem>()
  val itemBinding = ItemBinding.of<ScanResultItem>(BR.item, R.layout.item_scan_result)
  
  val connectedDevices = ObservableArrayList<ConnectedBleDevice>()
  
  private val viewModel: HomeViewModel by inject()
  private val deviceConnection: DeviceConnection by inject()
  
  private val olympusNotificationManager: OlympusNotificationManager by inject()
  
  private val scannerDisposable = CompositeDisposable()
  private val connectDisposable = CompositeDisposable()
  
  private val startScanRelay: PublishRelay<Any> = PublishRelay.create()
  private val connectPeripheralRelay: PublishRelay<String> = PublishRelay.create()
  
  private var dialogController: DialogController? = null
  private var imageDialogController: ImageDialogController? = null
  
  val updateAnimation = ObservableBoolean(false)
  val isScanning = ObservableBoolean(false)
  
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
    
    connectDisposable.add(viewModel.connectBleDevice(this))
  }
  
  override fun render(): Function<Observable<HomeUiModel>, Disposable> {
    return RxUi.ui { uiModel ->
      
      Timber.d("uiModel: $uiModel")
      
      when (uiModel) {
        is HomeUiModel.Disconnected -> {
          // make sure original connections are disposed of
          connectDisposable.clear()
          isConnected.set(false)
          
          changeUiState(UiState.READY_TO_SCAN)
        }
        is HomeUiModel.BluetoothIssue -> {
          when (uiModel.bluetoothState) {
            BluetoothState.BLUETOOTH_NOT_AVAILABLE -> {
            }
            BluetoothState.LOCATION_PERMISSION_NOT_GRANTED -> {
              showPermissionDialog()
            }
            BluetoothState.BLUETOOTH_NOT_ENABLED -> {
              changeUiState(UiState.BLUETOOTH_NOT_ENABLED)
            }
            BluetoothState.LOCATION_SERVICES_NOT_ENABLED -> {
            
            }
            BluetoothState.UNDEFINED -> {
            
            }
            BluetoothState.READY -> {
              changeUiState(UiState.READY_TO_SCAN)
            }
          }
        }
        is HomeUiModel.ConnectedDevicesUpdate -> {
          connectedDevices.clear()
          connectedDevices.addAll(uiModel.list.map { ConnectedBleDevice(it) })
        }
        is HomeUiModel.ScannerResult -> {
          //changeUiState(UiState.SCANNING)
          
          if (!items.any { it.scanResult.macAddress == uiModel.scanResult.macAddress }) {
            items.add(ScanResultItem(uiModel.scanResult, this))
          }
        }
        is HomeUiModel.StatusUpdate -> {
          olympusNotificationManager.send(uiModel.state)
          
          powerLevel.set(uiModel.bleDevice.peripheral?.powerLevel)
          isConnected.set(uiModel.bleDevice.isConnected())
          
          uiModel.bleDevice.peripheral?.transmitter?.rawRotationalAngle?.let {
            rawRotationAngle.set(it)
          }
          
          uiModel.bleDevice.peripheral?.patch?.let {
            currentPatch.set(it)
          }
          
          changeUiState(uiModel.state)
          // TODO - check overlay state too once we are ready
        }
        is HomeUiModel.RawDataPackageUpdate -> {
          updateAnimation.set(true)
          updateAnimation.notifyChange()
          lastCheck.set(uiModel.lastUpdate)
          
          // TODO - store rawDataPackage to send to the server at a later point
        }
      }
    }
  }
  
  private fun changeUiState(newState: UiState) {
    if (currentUiState == newState) {
      Timber.d("Same state was hit [${newState}] - Skipping")
    }
    else {
      val routerTransaction = when (newState) {
        UiState.OK_A -> {
          OkController.routerTransaction()
        }
        UiState.INNER_BREACH_B -> {
          InnerBreachController.routerTransaction()
        }
        UiState.OUTER_BREACH_C -> {
          OuterBreachController.routerTransaction()
        }
        UiState.CUTGUARD_BROKEN_I -> {
          CutGuardController.routerTransaction()
        }
        UiState.WATER_INGRESS_DETECTION_J -> {
          WaterIngressDetectionController.routerTransaction()
        }
        UiState.TRANSMITTER_DETACHED_V -> {
          TransmitterDetachedController.routerTransaction()
        }
        UiState.TRANSMITTER_BATTERY_CHARGING_W -> {
          TransmitterChargingController.routerTransaction()
        }
        UiState.BLUETOOTH_NOT_ENABLED,
        UiState.DISCONNECTED -> {
          BluetoothNotEnabledController.routerTransaction()
        }
        UiState.INITIAL -> {
          UnknownController.routerTransaction()
        }
        UiState.READY_TO_SCAN -> {
          isScanning.set(false)
          
          // the scanner observable should be disposed when we start connecting to a bledevice
          // this is why it's added to it's own disposable
          scannerDisposable.add(viewModel.bindScanner(this))
          
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
        }
        
        if (stateRouter?.hasRootController() == true) {
          stateRouter?.replaceTopController(it)
        }
        else {
          stateRouter?.setRoot(it)
        }
      }
    }
    
    currentUiState = newState
  }
  
  val patchClickedListener = object : PatchView.Listener {
    override fun onPatchClicked(patch: Patch) {
      Timber.e("Patch was clicked")
    }
  }
  
  fun onButtonGetStartedGuideClick(){
    
    router.pushController(
      GetStartedGuideController.routerTransaction()
        .popChangeHandler(VerticalChangeHandler())
        .pushChangeHandler(VerticalChangeHandler())
    )
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
      getBinding().drawerLayout.closeDrawer(GravityCompat.END, true)
    }
    else {
      getBinding().drawerLayout.openDrawer(GravityCompat.END, true)
    }
  }
  
  fun enableBluetoothClicked() {
    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).also {
      startActivityForResult(it, REQUEST_CODE_BLUETOOTH_ENABLE)
    }
  }
  
  fun onScanClicked() {
    isScanning.set(true)
    startScanRelay.accept("")
  }
  
  override fun onScanResultClicked(scanResult: ScanResult) {
    macAddress = scanResult.macAddress
    
    // dispose the observable that do the scanning - at this point it is no longer needed
    scannerDisposable.clear()
    connectPeripheralRelay.accept(scanResult.macAddress)
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
  
  override fun onScanRequested(): Observable<Any> {
    return startScanRelay.publish()
      .refCount()
  }
  
  override fun onConnectPeripheral(): Observable<String> {
    return connectPeripheralRelay.publish()
      .refCount()
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
  
  companion object {
    const val TAG_CONTROLLER = "home_controller"
    
    private const val REQUEST_CODE_PERMISSION_LOCATION = 5001
    private const val REQUEST_CODE_PERMISSION_RATIONALE = 5002
    
    const val REQUEST_CODE_BLUETOOTH_ENABLE = 5005
    
    fun routerTransaction() = RouterTransaction.with(HomeController())
      .tag(TAG_CONTROLLER)
  }
  
}