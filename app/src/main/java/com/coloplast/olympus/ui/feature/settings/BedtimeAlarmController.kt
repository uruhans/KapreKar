package com.coloplast.olympus.ui.feature.settings

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerBedtimeAlarmBinding
import com.coloplast.olympus.shared.preferences.PreferencesImpl
import com.coloplast.olympus.shared.types.Sensors
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.dialogs.TimePickerDialogController
import com.coloplast.olympus.ui.common.extensions.onValueChanged
import org.koin.core.inject
import java.time.LocalTime


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 04/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class BedtimeAlarmController: BaseController<ControllerBedtimeAlarmBinding>(),
  TimePickerDialogController.Listener {

  private val preferences: PreferencesImpl by inject()

  private var dialog: TimePickerDialogController? = null

  val allSensorsSelected = ObservableBoolean(preferences.bedtimeAlertSensor == Sensors.ALL)
  val timeStrings = ObservableArrayList<Any>()
  val start = ObservableField<LocalTime>(preferences.bedtimeStart).apply {
    compositeDisposable.add(
      onValueChanged { this@BedtimeAlarmController.timeStrings[0] = get()!!.toString() }
    )
  }
  val end = ObservableField<LocalTime>(preferences.bedtimeEnd).apply {
      compositeDisposable.add(
        onValueChanged { this@BedtimeAlarmController.timeStrings[1] = get()!!.toString() }
      )
    }

  override fun attachVariables(viewBinding: ControllerBedtimeAlarmBinding) {
    viewBinding.controller = this

    // Initialize string arguments.
    timeStrings.add(start.get()!!.toString())
    timeStrings.add(end.get()!!.toString())
  }

  override fun onViewBound(viewBinding: ControllerBedtimeAlarmBinding) {
    //noop
  }

  override fun onTimeSelected(requestCode: Int, hour: Int, minute: Int) {
    when(requestCode){
      TIME_START_REQUEST_CODE -> {
        start.set(LocalTime.of(hour, minute))
      }
      TIME_END_REQUEST_CODE -> end.set(LocalTime.of(hour,minute))
    }
    dialog?.onDismiss()
    dialog = null
  }

  fun pickStartTime(){
      dialog = TimePickerDialogController.create(
        this,
        TIME_START_REQUEST_CODE,
        start.get()!!.hour,
        start.get()!!.minute,
        true,
        true
      ).apply { show() }
  }

  fun pickEndTime(){
    dialog = TimePickerDialogController.create(
      this,
      TIME_END_REQUEST_CODE,
      end.get()!!.hour,
      end.get()!!.minute,
      true,
      true
    ).apply { show() }
  }

  fun onSaveChanges(){
    // Save time window
    preferences.bedtimeStart = start.get()!!
    preferences.bedtimeEnd = end.get()!!

    // Save selected sensor
    preferences.bedtimeAlertSensor = when(allSensorsSelected.get()) {
      true -> Sensors.ALL
      false -> Sensors.OUTER
    }
    //TODO save selection
    parentController?.handleBack()
  }

  fun onDiscard() = parentController?.handleBack()

  override fun getLayout(): Int = R.layout.controller_bedtime_alarm

  companion object{

    private const val TAG_CONTROLLER = "bedtime_alarm_controller"
    private const val TIME_START_REQUEST_CODE = 1001
    private const val TIME_END_REQUEST_CODE = 1002

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(BedtimeAlarmController()).tag(TAG_CONTROLLER)
    }
  }
}