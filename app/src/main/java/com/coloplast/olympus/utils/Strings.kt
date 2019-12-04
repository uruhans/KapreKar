package com.coloplast.olympus.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.coloplast.olympus.R
import com.coloplast.olympus.shared.AppSettings
import com.coloplast.olympus.ui.common.extensions.LocalDateTimeHelper
import org.threeten.bp.*
import org.threeten.bp.format.TextStyle
import timber.log.Timber
import java.io.InputStream
import java.text.DateFormat
import java.util.*

object Strings {
  
  fun parse(text: Any): String {
    when (text) {
      is String -> {
        return text
      }
      is Int -> {
        return AppSettings.applicationContext.getString(text)
      }
    }
    
    Timber.e("Could not parse item of type: $text")
    return ""
  }
  
  /**
   * Colorize text with alternating colors every time a ^ appears
   * It's possible to have multiple ^ in the text
   *
   * default colors
   * InitialColor = R.color.blue
   * AlternatingColor = R.color.green
   *
   * @param text the text to colorize (will change color every time it hit's a ^)
   * @return a colorized spannable to be used directly on a TextView
   */
  fun colorizedSpannable(text: String): Spannable {
    val initialColor = ContextCompat.getColor(AppSettings.applicationContext, R.color.colorPrimary)
    val alternatingColor = ContextCompat.getColor(AppSettings.applicationContext, R.color.colorAccent)
    
    return colorizedSpannable(text, initialColor, alternatingColor)
  }
  
  
  /**
   * <pre>
   * Colorize text with alternating colors every time a ^ appears
   * It's possible to have multiple ^ in the text
   * </pre>
   *
   * @param text the text to colorize (will change color every time it hit's a ^)
   * @param initialColor specify the initial text color (must be a ColorInt)
   * @param alternatingColor specify the alternating text color (must be a ColorInt)
   * @return a colorized spannable to be used directly on a TextView
   */
  fun colorizedSpannable(text: String, @ColorInt initialColor: Int, @ColorInt alternatingColor: Int): Spannable {
    if (!text.contains("^")) {
      return SpannableString(text)
    }
    
    val originalSplit = text.split("^")
    val builder = SpannableStringBuilder()
    
    var currentColor = initialColor
    var span: SpannableString
    
    originalSplit.forEach {
      span = SpannableString(it)
      
      span.setSpan(
        ForegroundColorSpan(currentColor), 0, it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
      
      builder.append(span)
      currentColor = if (currentColor == initialColor) alternatingColor else initialColor
    }
    
    return builder
  }
  
  fun getStringFromAssets(filename: String): String {
    return getFromAssets(filename)
  }
  
  private fun getFromAssets(filename: String): String {
    Timber.d("getFromAssets: $filename")
    return try {
      val inputStream: InputStream = AppSettings.applicationContext.resources.assets.open(filename)
      inputStream.bufferedReader()
        .use { it.readText() }
    }
    catch (e: Exception) {
      Timber.e("getFromAssets: $e")
      ""
    }
  }
  
  fun getTimeString(dt: LocalDateTime): String {
    val formatter = DateFormat.getTimeInstance(DateFormat.SHORT)
    return formatter.format(dt.atZone(ZoneOffset.UTC))
  }
  
  fun getTimeString(millis: Long) : String {
    return getTimeString(convertMillisToTime(millis))
  }
  
  fun getNiceDate(dateTime: LocalDateTime): String {
    val formatter = android.text.format.DateFormat.getMediumDateFormat(AppSettings.applicationContext)
    return formatter.format(dateTime)
  }
  
  fun getNiceLongDateWithDay(dateTime: LocalDateTime): String {
    val formatter = android.text.format.DateFormat.getLongDateFormat(AppSettings.applicationContext)
    return "${DayOfWeek.of(dateTime.dayOfWeek.value).getDisplayName(
      TextStyle.FULL_STANDALONE,
      Locale.getDefault()
    )}, ${formatter.format(dateTime)}"
  }
  
  fun getNiceShortDayAndMonthWithTime(dateTime: LocalDateTime): String {
    val formatter = android.text.format.DateFormat.getMediumDateFormat(AppSettings.applicationContext)
    
    val date = "${DayOfWeek.of(dateTime.dayOfWeek.value).getDisplayName(
      TextStyle.SHORT_STANDALONE,
      Locale.getDefault()
    )}, ${formatter.format(dateTime)}"
    
    val time = getTimeString(dateTime)
    
    return "$date $time"
  }
  
  fun getNiceDateTime(dateTime: LocalDateTime): String {
    val date = getNiceDate(dateTime)
    val time = getTimeString(dateTime)
    
    return "$date $time"
  }
  
  fun convertTimeToMillis(hours: Int, minutes: Int): Long {
    return (hours * 1000 * 60 * 60).toLong()
      .plus(minutes * 60 * 1000)
  }
  
  fun convertMillisToTime(millis: Long): LocalDateTime {
    return LocalDateTimeHelper.ofMillis(millis)
  }
}