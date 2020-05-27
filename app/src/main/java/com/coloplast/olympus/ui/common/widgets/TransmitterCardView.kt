package com.coloplast.olympus.ui.common.widgets

import android.R.attr
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.marginEnd
import androidx.core.view.updateMargins
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ViewTransmitterCardBinding
import com.coloplast.olympus.ui.feature.transmitters.models.TransmitterItem


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 19/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class TransmitterCardView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr){

  private val inflater = LayoutInflater.from(context)
  private val viewBinding = ViewTransmitterCardBinding.inflate(inflater, this, true).apply {
    background = context.getDrawable(R.drawable.shape_deletable_cardview_background)
  }

  fun setEditState(state: Boolean){
    // X Axis difference from original position
    val xAxisDiff = when(state){
      true -> -viewBinding.deleteButton.width.toFloat()
      false -> 0f
    }
    // Animation
    ObjectAnimator.ofFloat(viewBinding.cardTransmitter, "translationX", xAxisDiff).apply {
      duration = 300
      start()
    }
  }

  fun setTransmitterItem(transmitterItem: TransmitterItem){
    viewBinding.item = transmitterItem
  }
}