package com.coloplast.olympus.ui.feature.transmitters.add.ble1

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAddTransmitterBle1Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener
import timber.log.Timber

class AddTransmitterBle1Controller : BaseController<ControllerAddTransmitterBle1Binding>() {

  override fun attachVariables(viewBinding: ControllerAddTransmitterBle1Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerAddTransmitterBle1Binding) {
    /* no-op */
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      REQUEST_CODE_BLUETOOTH_ENABLE -> {
        if (resultCode == Activity.RESULT_CANCELED) {
          Timber.d("enable it, dude")
        }
        else {
          getListener(FlowListener::class.java)?.onNextClicked()
        }
      }
    }
  }

  fun onNextClicked() {
    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).also {
      startActivityForResult(it, REQUEST_CODE_BLUETOOTH_ENABLE)
    }
  }

  override fun getLayout(): Int {
    return R.layout.controller_add_transmitter_ble1
  }

  companion object {
    private const val CONTROLLER_TAG = "add_transmitter_ble1_controller"
    private const val REQUEST_CODE_BLUETOOTH_ENABLE = 5005

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(AddTransmitterBle1Controller()).tag(CONTROLLER_TAG)
    }
  }


}