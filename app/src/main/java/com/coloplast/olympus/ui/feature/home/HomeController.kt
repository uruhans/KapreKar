package com.coloplast.olympus.ui.feature.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.models.BluetoothState
import com.coloplast.olympus.bluetooth.businesslayer.models.PowerLevel
import com.coloplast.olympus.bluetooth.businesslayer.models.ScanResult
import com.coloplast.olympus.databinding.ControllerHomeBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.PermissionHandler
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.dialogs.ImageDialogController
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import com.coloplast.olympus.ui.feature.home.models.ScanResultItem
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.koin.standalone.inject
import org.threeten.bp.LocalDateTime
import timber.log.Timber

class HomeController : BaseController<ControllerHomeBinding>, HomeView, PermissionHandler, BaseDialogController.Listener, ScanResultItem.Listener {
  
  val items = ObservableArrayList<ScanResultItem>()
  val itemBinding = ItemBinding.of<ScanResultItem>(BR.item, R.layout.item_scan_result)
  
  private val viewModel: HomeViewModel by inject()
  
  private val scannerDisposable = CompositeDisposable()
  
  private val startScanRelay: PublishRelay<Any> = PublishRelay.create()
  private val connectPeripheralRelay: PublishRelay<String> = PublishRelay.create()
  
  private var dialogController: DialogController? = null
  private var imageDialogController: ImageDialogController? = null
  
  val readyToScan = ObservableBoolean(false)
  val isScanning = ObservableBoolean(false)
  
  val powerLevel = ObservableField<PowerLevel>()
  val isConnected = ObservableBoolean(false)
  val lastCheck = ObservableField<LocalDateTime>()
  
  constructor() : super()
  constructor(args: Bundle?) : super(args)
  
  override fun attachVariables(viewBinding: ControllerHomeBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerHomeBinding) {
    compositeDisposable.add(viewModel.bind(this))
    scannerDisposable.add(viewModel.bindScanner(this))
  }
  
  override fun render(): Function<Observable<HomeUiModel>, Disposable> {
    return RxUi.ui { uiModel ->
      
      Timber.d("uiModel: $uiModel")
      
      when (uiModel) {
        is HomeUiModel.BluetoothIssue -> {
          when (uiModel.bluetoothState) {
            BluetoothState.BLUETOOTH_NOT_AVAILABLE -> {
            
            }
            BluetoothState.LOCATION_PERMISSION_NOT_GRANTED -> {
              showPermissionDialog()
            }
            BluetoothState.BLUETOOTH_NOT_ENABLED -> {
              readyToScan.set(false)
            }
            BluetoothState.LOCATION_SERVICES_NOT_ENABLED -> {
            
            }
            BluetoothState.UNDEFINED -> {
            
            }
            BluetoothState.READY -> {
              readyToScan.set(true)
            }
          }
        }
        is HomeUiModel.Result -> {
          if (!items.any { it.scanResult.macAddress == uiModel.scanResult.macAddress }) {
            items.add(ScanResultItem(uiModel.scanResult, this))
          }
        }
        is HomeUiModel.StatusUpdate -> {
          powerLevel.set(uiModel.bleDevice.peripheral?.powerLevel)
          isConnected.set(uiModel.bleDevice.isConnected())
          lastCheck.set(uiModel.lastUpdate)
        }
      }
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
        requestPermission(this, REQUEST_CODE_PERMISSION_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
      }
      REQUEST_CODE_PERMISSION_RATIONALE -> {
        requestPermissionAgain(this, REQUEST_CODE_PERMISSION_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
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
    getBinding().drawerLayout.openDrawer(GravityCompat.END, true)
  }
  
  fun enableBluetoothClicked() {
    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).also {
      startActivityForResult(it, REQUEST_CODE_BLUETOOTH_ENABLE)
    }
  }
  
  override fun onScanResultClicked(scanResult: ScanResult) {
    if (!scannerDisposable.isDisposed) {
      scannerDisposable.dispose()
    }
    connectPeripheralRelay.accept(scanResult.macAddress)
  }
  
  fun onScanClicked() {
    isScanning.set(true)
    startScanRelay.accept("")
  }
  
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      REQUEST_CODE_BLUETOOTH_ENABLE -> {
        if (resultCode == REQUEST_CODE_BLUETOOTH_ENABLE) {
          Timber.d("are you dumb? enable it, dude")
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
  
  companion object {
    private const val TAG_CONTROLLER = "home_controller"
    
    private const val REQUEST_CODE_PERMISSION_LOCATION = 5001
    private const val REQUEST_CODE_PERMISSION_RATIONALE = 5002
    private const val REQUEST_CODE_BLUETOOTH_ENABLE = 5005
    
    fun routerTransaction() = RouterTransaction.with(HomeController()).tag(TAG_CONTROLLER)
  }
}