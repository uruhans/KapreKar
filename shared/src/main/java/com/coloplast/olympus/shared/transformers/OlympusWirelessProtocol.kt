package com.coloplast.olympus.shared.transformers

import com.coloplast.olympus.shared.models.ClassifierSettings
import com.coloplast.olympus.shared.models.RawDataPackage
import com.coloplast.olympus.shared.models.StatusPackage
import com.coloplast.olympus.shared.models.TransmitterSettings

object OlympusWirelessProtocol {

  fun settingsTransformer(value: ByteArray): TransmitterSettings {
    val wid_R_threshold =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 0).toUShort()
    val wid_R_hysteresis =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 2).toUShort()
    val threshold_cs0 =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 4).toUShort()
    val threshold_cs1 =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 6).toUShort()
    val threshold_cs2 =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 8).toUShort()
    val threshold_cs3 =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 10).toUShort()
    val threshold_cs4 =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 12).toUShort()
    val threshold_cs5 =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 14).toUShort()
    val wid_C_threshold =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 16).toUShort()
    val csHysteresis =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 18).toUShort()
    val batterythreshold = value[20].toUByte()
    val batteryhysteresis = value[21].toUByte()
    val sampleInterval = value[22].toUByte()
    val filterType = value[23].toUByte()

    return TransmitterSettings(
      wid_R_threshold,
      wid_R_hysteresis,
      threshold_cs0,
      threshold_cs1,
      threshold_cs2,
      threshold_cs3,
      threshold_cs4,
      threshold_cs5,
      wid_C_threshold,
      csHysteresis,
      batterythreshold,
      batteryhysteresis,
      sampleInterval,
      filterType
    )
  }

  fun classifierSettingsTransformer(value: ByteArray): ClassifierSettings {
    val ird_ll = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 0).toUShort()
    val dv_noise_ll =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_SINT16, 2).toShort()
    val ird_spike_ll =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_SINT16, 4).toShort()
    val ird_leak_ul =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 6).toUShort()
    val ird_pa_ul =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 8).toUShort()
    val signal_sweat_ll =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 10).toUShort()
    val signal_blinded_ll =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 12).toUShort()
    val c_lsl_0 = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 14).toUShort()
    val c_lsl_1 = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 16).toUShort()
    val c_lsl_2 = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 18).toUShort()
    val c_lsl_3 = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 20).toUShort()
    val c_lsl_4 = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 22).toUShort()
    val c_lsl_5 = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 24).toUShort()
    val state_hold = value[26].toUByte()
    val hist_size = value[27].toUByte()
    val noise_count_ul = value[28].toUByte()
    val no_noise_count_ul = value[29].toUByte()

    return ClassifierSettings(
      ird_ll,
      dv_noise_ll,
      ird_spike_ll,
      ird_leak_ul,
      ird_pa_ul,
      signal_sweat_ll,
      signal_blinded_ll,
      c_lsl_0,
      c_lsl_1,
      c_lsl_2,
      c_lsl_3,
      c_lsl_4,
      c_lsl_5,
      state_hold,
      hist_size,
      noise_count_ul,
      no_noise_count_ul
    )
  }

  fun statusPackageTransformer(value: ByteArray): StatusPackage {
    val sensor0 = value[0].toUByte()
    val sensor1 = value[1].toUByte()
    val sensor2 = value[2].toUByte()
    val sensor3 = value[3].toUByte()
    val sensor4 = value[4].toUByte()
    val sensor5 = value[5].toUByte()

    val waterIngressDetection = value[6].toUByte()
    val attached = value[7].toUByte()
    val patchType = value[8].toUByte()
    val compromised = value[9].toUByte()

    val rawRotationalAngle = value[10].toInt()
    val batteryState = value[11].toUByte()

    val fwWarning = value[12].toInt()
    val fwError = value[13].toInt()

    return StatusPackage(
      sensor0, sensor1, sensor2, sensor3, sensor4, sensor5,
      waterIngressDetection, attached, patchType, compromised,
      rawRotationalAngle, batteryState, fwWarning, fwError
    )
  }

  fun rawDataPackageTransformer(value: ByteArray): RawDataPackage {
    val year = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 0).toUShort()
    val month = value[2].toUByte()
    val day = value[3].toUByte()
    val hour = value[4].toUByte()
    val minute = value[5].toUByte()
    val seconds = value[6].toUByte()
    val seconds256 = value[7].toUByte()

    val widR = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 8).toUShort()
    val s0SensorData =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 10).toUShort()
    val s1SensorData =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 12).toUShort()
    val s2SensorData =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 14).toUShort()
    val s3SensorData =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 16).toUShort()
    val s4SensorData =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 18).toUShort()
    val s5SensorData =
      ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 20).toUShort()
    val cs0Data = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 22).toUShort()
    val cs1Data = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 24).toUShort()
    val cs2Data = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 26).toUShort()
    val cs3Data = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 28).toUShort()
    val cs4Data = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 30).toUShort()
    val cs5Data = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 32).toUShort()
    val widC = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 34).toUShort()

    val battMv = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 36).toUShort()
    val battLevel = value[38].toUByte()

    val bleDisconnected = value[39].toUByte()
    val classifierReset = value[40].toInt()

    val accX = value[41].toInt()
    val accY = value[42].toInt()
    val accZ = value[43].toInt()

    val activity = value[44].toUByte()
    val wid = value[45].toInt()
    val attach = value[46].toInt()
    val patchId = value[47].toInt()
    val compromised = value[48].toInt()

    val angleRaw = value[49].toInt()
    val battPower = value[50].toInt()

    return RawDataPackage(
      year, month, day, hour, minute, seconds, seconds256,
      widR, s0SensorData, s1SensorData, s2SensorData, s3SensorData, s4SensorData, s5SensorData,
      cs0Data, cs1Data, cs2Data, cs3Data, cs4Data, cs5Data, widC,
      battMv, battLevel, bleDisconnected, classifierReset, accX, accY, accZ,
      activity, wid, attach, patchId, compromised, angleRaw, battPower
    )
  }

}