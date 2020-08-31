package com.coloplast.olympus.shared.transformers

import com.coloplast.olympus.shared.models.RawDataPackage
import com.coloplast.olympus.shared.models.TransmitterSettings
import io.reactivex.ObservableTransformer

object OlympusWirelessProtocol {

  // TODO - CPO-415: finish this
  val settingsTransformer = ObservableTransformer<ByteArray, TransmitterSettings> { upstream ->
    upstream
      .map {

        val wid_R_threshold =
          ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 0).toUShort()
        val wid_R_hysteresis =
          ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 2).toUShort()
        val threshold_cs0 =
          ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 4).toUShort()
        val threshold_cs1 =
          ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 6).toUShort()
        val threshold_cs2 =
          ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 8).toUShort()
        val threshold_cs3 =
          ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 10).toUShort()
        val threshold_cs4 =
          ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 12).toUShort()
        val threshold_cs5 =
          ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 14).toUShort()
        val wid_C_threshold =
          ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 16).toUShort()
        val csHysteresis =
          ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 18).toUShort()
        val batterythreshold = it[20].toUByte()
        val batteryhysteresis = it[21].toUByte()
        val sampleInterval = it[22].toUByte()
        val filterType = it[23].toUByte()

        TransmitterSettings()
      }
  }

  // todo - return proper object when needed
  val classifierSettingsTransformer = ObservableTransformer<ByteArray, ByteArray> { upstream ->
    upstream.map {
      val ird_ll = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 0).toUShort()
      val dv_noise_ll =
        ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_SINT16, 2).toShort()
      val ird_spike_ll =
        ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_SINT16, 4).toShort()
      val ird_leak_ul =
        ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 6).toUShort()
      val ird_pa_ul = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 8).toUShort()
      val signal_sweat_ll =
        ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 10).toUShort()
      val signal_blinded_ll =
        ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 12).toUShort()
      val c_lsl_0 = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 14).toUShort()
      val c_lsl_1 = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 16).toUShort()
      val c_lsl_2 = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 18).toUShort()
      val c_lsl_3 = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 20).toUShort()
      val c_lsl_4 = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 22).toUShort()
      val c_lsl_5 = ValueInterpreter.getIntValue(it, ValueInterpreter.FORMAT_UINT16, 24).toUShort()
      val state_hold = it[26].toUByte()
      val hist_size = it[27].toUByte()
      val noise_count_ul = it[28].toUByte()
      val no_noise_count_ul = it[29].toUByte()

      it
    }
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
    val s0SensorData = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 10).toUShort()
    val s1SensorData = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 12).toUShort()
    val s2SensorData = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 14).toUShort()
    val s3SensorData = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 16).toUShort()
    val s4SensorData = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 18).toUShort()
    val s5SensorData = ValueInterpreter.getIntValue(value, ValueInterpreter.FORMAT_UINT16, 20).toUShort()
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