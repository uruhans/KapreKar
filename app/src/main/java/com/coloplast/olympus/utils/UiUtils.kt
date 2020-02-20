package com.coloplast.olympus.utils

import android.util.TypedValue
import com.coloplast.olympus.shared.AppSettings
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.format.ResolverStyle
import org.threeten.bp.temporal.ChronoField

object UiUtils {
  
  const val INVALID_INT = -1
  
  private const val ROTATION_180_DEGREES = 64 // because transmitter values are rotated 180 degrees
  private const val FULL_ROTATION = 128
  private const val SECTOR_SIZE = 16
  private const val SECTORS_PR_FULL_ROTATION = 8
  private const val DEGREES_PR_FULL_ROTATION = 360
  
  /**
   * angle128 = angle360 * 128 / 360
   *
   * - Og tilbage igen:
   * angle360 = angle128 * 360 / 128
   *
   * Du drejer samtidig vinklen 180 grader. Modulo ”en omgang” sørger for wrap-around, i.e. 360 grader -> 0 grader:
   * angleApp = ((angle360 + 180) % 360) = ((angle128 + 64) % 128) * 360 / 128
   * Derefter reduceres præcisionen med afrunding til nærmeste:
   * angleGUI = ((angle360 + 180 + 22.5) % 360) / 45 * 45 = ((angle128 + 64 + 8) % 128) / 16 * 16 * 360 / 128
   *
   * Lidt omrokering:
   * angleGUI = ((angle360 + 180 + 22.5) % 360) / 45 * 45 = ((angle128 + 64 + 8) % 128) / 16 * 360 * 16 / 128
   * angleGUI = ((angle360 + 180 + 22.5) % 360) / 45 * 45 = ((angle128 + 64 + 8) % 128) / 16 * 360 / 8
   *
   **/
  fun calculatePatchRotation(rawAngleRotation: Int) : Float {
    val orientation =
      (((rawAngleRotation.plus(ROTATION_180_DEGREES).plus((SECTOR_SIZE / 2))) % FULL_ROTATION)) / SECTOR_SIZE * DEGREES_PR_FULL_ROTATION / SECTORS_PR_FULL_ROTATION
  
    return orientation.toFloat()
  }
  
  fun Int.dpToPx() : Float {
    return this.toFloat().dpToPx()
  }
  
  fun Float.dpToPx() : Float {
    return TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      this,
      AppSettings.applicationContext.resources.displayMetrics
    )
  }

  val ISO_LOCAL_TIME_NO_NANO_OF_SECOND = (DateTimeFormatterBuilder().appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .optionalStart()
    .appendLiteral(':')
    .appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter())
    
}