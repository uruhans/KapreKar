package com.coloplast.olympus.ui.feature.settings

import androidx.databinding.ObservableBoolean
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerSettingsBinding
import com.coloplast.olympus.shared.preferences.Preferences
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.common.extensions.onValueChanged
import com.coloplast.olympus.utils.OSUtils
import org.koin.core.inject
import java.util.*


class SettingsController: BaseController<ControllerSettingsBinding>() {

  interface Listener{
    fun setBedtimeAlarm()
    fun chooseSensors()
  }

  private val preferences: Preferences by inject()

  val ringingAlertsEnabled = ObservableBoolean().apply {
    set(preferences.areRingingAlertsEnabled)
    compositeDisposable.add(
      onValueChanged { preferences.areRingingAlertsEnabled = this.get() }
    )
  }

  override fun attachVariables(viewBinding: ControllerSettingsBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerSettingsBinding) {
    //noop
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

  companion object{

    private const val TAG_CONTROLLER = "settings_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SettingsController()).tag(TAG_CONTROLLER)
    }
  }
}
