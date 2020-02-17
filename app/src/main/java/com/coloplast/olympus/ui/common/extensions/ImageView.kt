package com.coloplast.olympus.ui.common.extensions

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
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
  powerLevel?.let {
    when (it) {
      PowerLevel.HIGH -> setImageResource(R.drawable.ic_battery_full_24dp)
      PowerLevel.MEDIUM -> setImageResource(R.drawable.ic_battery_medium_24dp)
      PowerLevel.LOW -> setImageResource(R.drawable.ic_battery_low_24dp)
      PowerLevel.NEAR_EMPTY -> setImageResource(R.drawable.ic_battery_empty_24dp)
    }
    
//    if (it == PowerLevel.NEAR_EMPTY) {
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
    val color = ContextCompat.getColor(context, colorRes)
    
    if (Build.VERSION.SDK_INT >= 29) {
      colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
    }
    else {
      setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
  }
}

