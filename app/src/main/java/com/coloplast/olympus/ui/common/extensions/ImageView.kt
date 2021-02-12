package com.coloplast.olympus.ui.common.extensions

import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.enums.BatteryState
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel
import com.coloplast.olympus.bluetooth.businesslayer.models.Battery

@BindingAdapter("resource")
fun ImageView.setResource(@DrawableRes resource: Int?) {
  resource?.let {
    setImageResource(it)
  }
}

@BindingAdapter("battery")
fun ImageView.setBattery(battery: Battery?) {
  val drawableId = when (battery?.state) {
    BatteryState.CHARGING -> R.drawable.ic_battery_charging
    else -> when (battery?.powerLevel) {
      PowerLevel.HIGH -> R.drawable.ic_battery_full_24dp
      PowerLevel.MEDIUM -> R.drawable.ic_battery_medium_24dp
      PowerLevel.LOW -> R.drawable.ic_battery_low_24dp
      PowerLevel.EMPTY -> R.drawable.ic_battery_empty_24dp
      else -> R.drawable.empty
    }
  }

  setImageResource(drawableId)
}

@BindingAdapter("tintDrawable")
fun ImageView.tintDrawable(@ColorRes colorRes: Int?) {
  colorRes?.let {
    if (colorRes == 0) {
      return
    }

    val color = ContextCompat.getColor(context, colorRes)

    if (Build.VERSION.SDK_INT >= 29) {
      colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
    }
    else {
      setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
  }
}

@BindingAdapter("safeResource")
fun ImageView.setContent(content: Any?) {
  when (content) {
    is Bitmap -> setImageBitmap(content)
    is Uri -> setImageURI(content)
    is Int -> setResource(content)
    is Drawable -> setImageDrawable(content)
  }
}

@BindingAdapter("startScaleAnimation")
fun ImageView.startScaleAnimation(startScaleAnimation: Boolean = false) {
  if (startScaleAnimation) {
    val anim = ScaleAnimation(
      scaleX, 1.2f,  // Start and end values for the X axis scaling
      scaleY, 1.2f,  // Start and end values for the Y axis scaling
      Animation.RELATIVE_TO_SELF, 0.5f,
      Animation.RELATIVE_TO_SELF, 0.5f // Pivot point of Y scaling
    )

    anim.fillAfter = true // Needed to keep the result of the animation
    anim.repeatMode = Animation.REVERSE
    anim.repeatCount = Animation.INFINITE
    anim.duration = 1000
    startAnimation(anim)
  }
  else {
    if (animation != null && animation.hasStarted()) {
      animation.cancel()
      animation.reset()
    }
  }
}

