package com.coloplast.olympus.ui.common.dialogs

import android.os.Bundle
import com.bluelinelabs.conductor.Controller
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerDialogTimeofdayBinding
import timber.log.Timber


@Suppress("PrivatePropertyName")
class TimePickerDialogController(args: Bundle?) :
  BaseDialogController<ControllerDialogTimeofdayBinding>(args) {

  interface Listener : BaseDialogController.Listener {
    fun onTimeSelected(requestCode: Int, hour: Int, minute: Int)
  }

  private val dismissAllowed: Boolean = args!!.getBoolean(BUNDLE_DISMISS_ALLOWED)
  private val requestCode: Int = args!!.getInt(BUNDLE_REQUEST_CODE)
  private val use24Hour: Boolean = args!!.getBoolean(BUNDLE_24_HOUR_CLOCK)

  private var hour: Int = args!!.getInt(BUNDLE_HOUR, 0)
  private var minute: Int = args!!.getInt(BUNDLE_MINUTE, 0)

  override fun attachVariables(viewBinding: ControllerDialogTimeofdayBinding) {
    viewBinding.controller = this
  }

  @Suppress("SpellCheckingInspection")
  override fun onViewBound(viewBinding: ControllerDialogTimeofdayBinding) {
    val timepicker = viewBinding.timepicker

    // Not checked but this may give problems if API < 23.
    timepicker.hour = hour
    timepicker.minute = minute

    timepicker.setIs24HourView(use24Hour)

    timepicker.setOnTimeChangedListener { _, hourOfDay, minute ->
      this.hour = hourOfDay
      this.minute = minute
    }
  }

  fun onDoneClicked() {
    getListener(Listener::class.java)?.onTimeSelected(requestCode, hour, minute)
    super.dismissDialog(false)
  }

  fun onDismissClicked() {
    if (dismissAllowed) {
      super.dismissDialog(false)
    }
  }

  override fun onDismiss() {
    getListener(Listener::class.java)?.onDialogDismissed(requestCode)
  }

  override fun getLayout(): Int {
    return R.layout.controller_dialog_timeofday
  }

  companion object {
    private const val BUNDLE_REQUEST_CODE = "bundle_request_code"
    private const val BUNDLE_HOUR = "bundle_hour"
    private const val BUNDLE_MINUTE = "bundle_minute"
    private const val BUNDLE_24_HOUR_CLOCK = "bundle_24_hour_clock"
    private const val BUNDLE_DISMISS_ALLOWED = "bundle_dismiss_allowed"

    fun create(
      targetController: Controller,
      requestCode: Int,
      hour: Int = 0,
      minute: Int = 0,
      use24Hour: Boolean = true,
      dismissAllowed: Boolean = true
    ): TimePickerDialogController {
      val args = Bundle().apply {
        putString(BUNDLE_TARGET_INSTANCE_ID, targetController.instanceId)
        putInt(BUNDLE_REQUEST_CODE, requestCode)
        putInt(BUNDLE_HOUR, hour)
        putInt(BUNDLE_MINUTE, minute)
        putBoolean(BUNDLE_24_HOUR_CLOCK, use24Hour)
        putBoolean(BUNDLE_DISMISS_ALLOWED, dismissAllowed)
      }

      if (targetController !is Listener) {
        Timber.e("TargetController [$targetController] does not implement Listener")
      }

      return TimePickerDialogController(args)
    }
  }
}