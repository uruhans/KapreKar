package com.coloplast.olympus.ui.feature.home

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import com.artemzin.rxui.RxUi
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.models.BluetoothState
import com.coloplast.olympus.databinding.ControllerHomeBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.PermissionHandler
import com.coloplast.olympus.ui.common.dialogs.BaseDialogController
import com.coloplast.olympus.ui.common.dialogs.DialogController
import com.coloplast.olympus.ui.feature.home.models.HomeUiModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import org.koin.standalone.inject
import timber.log.Timber

class HomeController : BaseController<ControllerHomeBinding>, HomeView, PermissionHandler, BaseDialogController.Listener {
  
  private val viewModel: HomeViewModel by inject()
  
  private var dialogController: DialogController? = null
  
  constructor() : super()
  constructor(args: Bundle?) : super(args)
  
  override fun attachVariables(viewBinding: ControllerHomeBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerHomeBinding) {
    compositeDisposable.add(viewModel.bind(this))
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
            
            }
            BluetoothState.LOCATION_SERVICES_NOT_ENABLED -> {
            
            }
            BluetoothState.READY, BluetoothState.UNDEFINED -> {
              // no-op
            }
          }
        }
      }
    }
  }
  
  override fun onPositiveButtonClicked(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_PERMISSION_LOCATION -> {
        dialogController?.dismissDialog(withCallback = true)
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
      else -> {
        super.onDialogDismissed(requestCode)
      }
    }
  }
  
  override fun showPermissionRationale(requestCode: Int) {
    when (requestCode) {
      REQUEST_CODE_PERMISSION_LOCATION -> {
        Timber.d("showPermissionRationale")
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
  
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when(requestCode) {
      REQUEST_CODE_BLUETOOTH_ENABLE -> {
        if(resultCode == REQUEST_CODE_BLUETOOTH_ENABLE) {
          Timber.d("are you dumb? enable it, dude")
        }
      }
      else -> super.onActivityResult(requestCode, resultCode, data)
    }
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_home
  }
  
  companion object {
    private const val TAG_CONTROLLER = "home_controller"
    
    private const val REQUEST_CODE_PERMISSION_LOCATION = 5001
    private const val REQUEST_CODE_BLUETOOTH_ENABLE = 5005
    
    fun routerTransaction() = RouterTransaction.with(HomeController()).tag(TAG_CONTROLLER)
  }
}