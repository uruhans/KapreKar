package com.coloplast.olympus.ui.common.widgets

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.viewpager2.widget.ViewPager2
import com.coloplast.olympus.databinding.ViewCodeInputBinding
import com.coloplast.olympus.databinding.ViewTransmitterCardBinding
import com.coloplast.olympus.shared.models.Transmitter
import com.coloplast.olympus.ui.common.extensions.toggle
import com.coloplast.olympus.ui.feature.transmitters.models.TransmitterItem


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 19/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class TransmitterCardView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr){

  private val inflater = LayoutInflater.from(context)
  private val view = ViewTransmitterCardBinding.inflate(inflater, this, true)

  fun setEditState(state: Boolean){
    // X Axis difference from original position
    val xAxisDiff = when(state){
      true -> -view.deleteButton.width.toFloat()
      false -> 0f
    }
    // Animation
    ObjectAnimator.ofFloat(view.cardTransmitter, "translationX", xAxisDiff).apply {
      duration = 300
      start()
    }
  }

  fun setTransmitterItem(transmitterItem: TransmitterItem){
    view.item = transmitterItem
  }

}