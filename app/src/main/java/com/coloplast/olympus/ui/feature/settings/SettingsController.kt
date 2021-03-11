package com.coloplast.olympus.ui.feature.settings

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerSettingsBinding
import com.coloplast.olympus.shared.preferences.PreferencesImpl
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.dialogs.ImageDialogController
import com.coloplast.olympus.utils.OSUtils
import org.koin.core.inject


class SettingsController: BaseController<ControllerSettingsBinding>(), ImageDialogController.Listener {

  interface Listener{
    fun setBedtimeAlarm()
    fun chooseSensors()
  }

  private val preferences: PreferencesImpl by inject()

  private var imageDialogController : ImageDialogController? = null

  val ringingAlertsEnabled = ObservableBoolean()
  val bedTimeAlertsEnabled = ObservableBoolean()

  override fun attachVariables(viewBinding: ControllerSettingsBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerSettingsBinding) {
    ringingAlertsEnabled.set(preferences.areRingingAlertsEnabled)
    ringingAlertsEnabled.addOnPropertyChangedCallback(ringingAlertsChanged)

    bedTimeAlertsEnabled.set(true)
  }

  private val ringingAlertsChanged = object: Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
      if(!ringingAlertsEnabled.get()) {
        showRingingAlertsOverlay()
      }

      preferences.areRingingAlertsEnabled = ringingAlertsEnabled.get()
    }
  }

  private fun showRingingAlertsOverlay() {
    imageDialogController = ImageDialogController.create(
      this,
      REQUEST_CODE_RINGING_OVERLAY,
      R.string.settings_sound_alerts_off,
      R.string.settings_sound_alerts_off_warning,
      R.string.generic_ok,
      topImage = R.drawable.ic_info_circle
    )

    imageDialogController?.show()
  }

  fun setBedtimeAlarmClicked(){
    getListener(Listener::class.java)!!.setBedtimeAlarm()
  }

  fun chooseSensors(){
    getListener(Listener::class.java)!!.chooseSensors()
  }

  fun openAppSettings(){
    val notificationChannel = activity!!.getString(R.string.lock_notification_channel_id)
    OSUtils.openAppSettings(activity!!, notificationChannel)
  }
  override fun getLayout(): Int = R.layout.controller_settings

  override fun onCloseClicked(requestCode: Int) {
    removeDialog()
  }

  override fun onPositiveButtonClicked(requestCode: Int) {
    removeDialog()
  }

  private fun removeDialog() {
    imageDialogController?.dismissDialog(false)
    imageDialogController = null
  }

  override fun onUnbind(viewBinding: ControllerSettingsBinding) {
    super.onUnbind(viewBinding)
    ringingAlertsEnabled.removeOnPropertyChangedCallback(ringingAlertsChanged)
  }

  companion object{
    private const val TAG_CONTROLLER = "settings_controller"
    private const val REQUEST_CODE_RINGING_OVERLAY = 1000

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SettingsController()).tag(TAG_CONTROLLER)
    }
  }
}
