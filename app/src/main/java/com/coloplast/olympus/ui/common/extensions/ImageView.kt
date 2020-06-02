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
import com.coloplast.olympus.bluetooth.businesslayer.enums.PowerLevel

@BindingAdapter("resource")
fun ImageView.setResource(@DrawableRes resource: Int?) {
  resource?.let {
    setImageResource(it)
  }
}

@BindingAdapter("powerLevel")
fun ImageView.setPowerLevel(powerLevel: PowerLevel?) {
  visibleOrInvisible(powerLevel != null)
  
  powerLevel?.let {
    when (it) {
      PowerLevel.HIGH -> setImageResource(R.drawable.ic_battery_full_24dp)
      PowerLevel.MEDIUM -> setImageResource(R.drawable.ic_battery_medium_24dp)
      PowerLevel.LOW -> setImageResource(R.drawable.ic_battery_low_24dp)
      PowerLevel.EMPTY -> setImageResource(R.drawable.ic_battery_empty_24dp)
    }
    
    //    if (it == PowerLevel.EMPTY) {
    //      val anim = AnimationUtils.loadAnimation(context, R.anim.blink)
    //      animation = anim
    //      animation.startNow()
    //    }
    //    else if (animation != null && animation.hasStarted()) {
    //      animation.cancel()
    //      animation.reset()
    //      animation = null
    //    }
  }
}

@BindingAdapter("tintDrawable")
fun ImageView.tintDrawable(@ColorRes colorRes: Int?) {
  colorRes?.let {
    if(colorRes == 0) {
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
  when(content) {
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
      scaleY,1.2f,  // Start and end values for the Y axis scaling
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
    if(animation != null && animation.hasStarted()) {
      animation.cancel()
      animation.reset()
    }
  }
}

