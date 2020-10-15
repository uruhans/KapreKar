package com.coloplast.olympus.utils

import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import com.coloplast.olympus.shared.AppSettings
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import kotlin.math.round

object UiUtils {
  
  const val INVALID_INT = -1
  private const val FULL_ROTATION = 128
  private const val DEGREES_PR_FULL_ROTATION = 360


  /**
   * https://jira.trifork.com/browse/CPO-480
   */
  fun calculatePatchRotation(rawAngleRotation: Int) : Int {
    val numberOfSectors = DEGREES_PR_FULL_ROTATION.div(45)
    val sectorSize = DEGREES_PR_FULL_ROTATION.div(numberOfSectors)
    val angle = rawAngleRotation.toDouble().times(DEGREES_PR_FULL_ROTATION).div(FULL_ROTATION)

    val closestSector = round(angle.div(sectorSize))
    val sectorAngle = sectorSize.times(closestSector)

    return sectorAngle.plus(180).toInt() % 360
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
  
  fun getColor(@ColorRes colorRes: Int) : Int {
    return AppSettings.applicationContext.getColor(colorRes)
  }

  val ISO_LOCAL_TIME_NO_NANO_OF_SECOND: DateTimeFormatter = (DateTimeFormatterBuilder().appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .optionalStart()
    .appendLiteral(':')
    .appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter())

  // Expand/Collapse methods from this SO answer:
  // https://stackoverflow.com/questions/4946295/android-expand-collapse-animation
  fun expand(v: View) {
    val matchParentMeasureSpec: Int =
      View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
    val wrapContentMeasureSpec: Int =
      View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
    val targetHeight: Int = v.measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    v.layoutParams.height = 1
    v.visibility = View.VISIBLE
    val a: Animation = object : Animation() {
      override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        v.layoutParams.height =
          if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
        v.requestLayout()
      }

      override fun willChangeBounds(): Boolean = true
    }

    // Expansion speed of 1dp/ms
    a.duration = (targetHeight / v.context.resources.displayMetrics.density).toLong()
    v.startAnimation(a)
  }

  fun collapse(v: View) {
    val initialHeight = v.measuredHeight

    val a = object: Animation(){
      override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        if(interpolatedTime == 1f){
          v.visibility = View.GONE
        } else {
          v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
          v.requestLayout()
        }
      }

      override fun willChangeBounds(): Boolean = true
    }

    // Collapse speed of 1dp/ms
    a.duration = (initialHeight / v.context.resources.displayMetrics.density).toLong()
    v.startAnimation(a)
  }
}