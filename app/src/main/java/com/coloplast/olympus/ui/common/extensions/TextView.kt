package com.coloplast.olympus.ui.common.extensions

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.*
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.coloplast.olympus.R
import com.coloplast.olympus.ui.common.listeners.AnimatorListenerImpl
import com.coloplast.olympus.utils.Strings
import com.coloplast.olympus.utils.UiUtils
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import timber.log.Timber
import java.util.*

@BindingAdapter("safeText")
fun TextView.setBindText(value: Any?) {
  if (value == null) {
    return
  }
  
  when (value) {
    is CharSequence -> text = value
    is Int -> {
      if (value != 0 && value != UiUtils.INVALID_INT) {
        text = context.getString(value)
      }
      else {
        Timber.w("Trying to set an invalid text resource [$value] on a TextView - is this on purpose?")
      }
    }
    else -> Timber.w("Trying to set an invalid text resource [$value] on a TextView - is this on purpose?")
  }
}

@BindingAdapter("textResId")
fun TextView.setBindText(value: Int) {
  setBindText(value as Any)
}

/**
 * colorize the text of a TextView
 */
@BindingAdapter("colorizeText")
fun TextView.colorizeText(shouldColorize: Boolean) {
  if (shouldColorize) {
    text = Strings.colorizedSpannable(text.toString())
  }
}

@BindingAdapter(
  value = ["android:text", "colorizeText", "primaryColor", "secondaryColor"], requireAll = true
)
fun TextView.colorizeText(
    newText: String?,
    shouldColorize: Boolean, @ColorRes primaryColor: Int, @ColorRes secondaryColor: Int
) {
  if (shouldColorize && newText != null) {
    text = Strings.colorizedSpannable(
      newText,
      ContextCompat.getColor(context, primaryColor),
      ContextCompat.getColor(context, secondaryColor)
    )
  }
}

@BindingAdapter("tintDrawable")
fun TextView.tintDrawable(color: Int) {
  compoundDrawablesRelative.forEach {
    if (Build.VERSION.SDK_INT >= 29) {
      it?.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
    }
    else {
      @Suppress("DEPRECATION") it?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
  }
}

@BindingAdapter(value = ["stringResource", "formatTextWithSingleArg"], requireAll = false)
fun TextView.format(@StringRes stringResource: Int, textArg: Any?) {
  text = context.getString(stringResource, textArg)
}

@BindingAdapter(value = ["stringResource", "formatTextWithArgs"], requireAll = false)
fun TextView.format(@StringRes stringResource: Int, formatArgs: ArrayList<Any>?) {
  if (stringResource == 0) {
    return
  }
  
  if (formatArgs == null || formatArgs.isEmpty()) {
    // If no format args was supplied, we display the string without args
    text = context.getString(stringResource)
    return
  }
  
  text = context.getString(stringResource, *formatArgs.toArray())
}

@BindingAdapter("textWithBoldMarkdown")
fun TextView.applyBold(@StringRes stringResource: Int) {
  val source = context.getString(stringResource)
  applyBold(source)
}

@BindingAdapter("textWithBoldMarkdown")
fun TextView.applyBold(source: String) {
  val splits = source.split("**")
  val spannableString = SpannableString(source.replace("**", ""))
  
  var startIndexOffset = 0
  for (i in splits.indices step 2) {
    if (i != splits.indices.last) {
      val start = startIndexOffset + splits[i].length
      val end = startIndexOffset + splits[i].length + splits[i + 1].length
      spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, 0)
      startIndexOffset = end
    }
  }
  
  text = spannableString
}

@BindingAdapter("html")
fun TextView.setHtmlText(value: Any?) {
  val html: String? = when (value) {
    is Int -> {
      context.getString(value)
    }
    is String -> {
      value.toString()
    }
    else -> {
      Timber.d("trying to set HTML with an invalid value: $value")
      null
    }
  }
  
  if (html.isNullOrBlank()) {
    return
  }
  
  text = if (Build.VERSION.SDK_INT >= 24) {
    Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
  }
  else {
    @Suppress("DEPRECATION") Html.fromHtml(html)
  }
}

@BindingAdapter("animateText")
fun TextView.setAnimateText(value: String) {
  if (value.isBlank()) {
    return
  }
  
  val propertyAnimator = animate().alpha(0f)
  propertyAnimator.duration = 200
  
  propertyAnimator.setListener(object : AnimatorListenerImpl {
    override fun onAnimationEnd(animation: Animator?) {
      text = value
      
      val reverse = animate().alpha(1f)
      reverse.duration = 200
      reverse.start()
    }
  })
  
  propertyAnimator.start()
}

@BindingAdapter("dateTime")
fun TextView.niceDateTime(dateTime: LocalDateTime) {
  text = Strings.getNiceDate(dateTime)
}

@BindingAdapter(value = ["dayOfWeek", "textStyle"], requireAll = true)
fun TextView.setDayOfWeek(dayOfWeek: DayOfWeek, textStyle: TextStyle) {
  text = dayOfWeek.getDisplayName(textStyle, Locale.getDefault())
}

@BindingAdapter("underline")
fun TextView.underlineText(underline: Boolean) {
  if (underline) {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
  }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("lastCheck")
fun TextView.lastCheck(localDateTime: LocalDateTime?) {
  localDateTime?.let {
    text = "${context.getString(R.string.status_last_check)} ${it.format(UiUtils.ISO_LOCAL_TIME_NO_NANO_OF_SECOND)}"
  }
}