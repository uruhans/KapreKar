package com.coloplast.olympus.ui.common.widgets

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.enums.SensorStatus
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.ui.common.extensions.visibleOrGoneFade
import com.coloplast.olympus.utils.UiUtils

class PatchView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  
  private val inflater = LayoutInflater.from(context)
  private val background = inflater.inflate(R.layout.view_imageview, this, false) as ImageView
  private var previousPatchState: Patch? = null
  
  private var isDetailView = false
  
  init {
    background.setImageResource(R.drawable.ic_detailed_plate_ok)
    addView(background)
    background.visibility = View.VISIBLE
  }
  
  fun isDetailView(isDetailView: Boolean = false) {
    this.isDetailView = isDetailView
  }
  
  fun setPatch(patch: Patch?) {
    patch?.let { newPatch ->
      var isSameState = false
      
      previousPatchState?.let {
        isSameState = it.isSameState(newPatch)
      }
      
      if (isSameState) {
        return@let
      }
      
      previousPatchState = newPatch
      
      val patchIcons = mutableListOf<Int>()
      
      if(isDetailView) {
        if (newPatch.outerSensor == SensorStatus.OK) {
          if (newPatch.innerSensor1 == SensorStatus.BREACH) {
            patchIcons.add(R.drawable.ic_detailed_plate_inner_nw)
          }
    
          if (newPatch.innerSensor2 == SensorStatus.BREACH) {
            patchIcons.add(R.drawable.ic_detailed_plate_inner_ne)
          }
    
          if (newPatch.innerSensor3 == SensorStatus.BREACH) {
            patchIcons.add(R.drawable.ic_detailed_plate_inner_s)
          }
        }
        else {
          if (newPatch.outerSensor == SensorStatus.BREACH && newPatch.innerSensorBreach.not()) {
            patchIcons.add(R.drawable.ic_detailed_plate_outer)
          }
          else {
            if (newPatch.innerSensor1 == SensorStatus.BREACH) {
              patchIcons.add(R.drawable.ic_detailed_plate_outer_inner_nw)
            }
      
            if (newPatch.innerSensor2 == SensorStatus.BREACH) {
              patchIcons.add(R.drawable.ic_detailed_plate_outer_inner_ne)
            }
      
            if (newPatch.innerSensor3 == SensorStatus.BREACH) {
              patchIcons.add(R.drawable.ic_detailed_plate_outer_inner_s)
            }
          }
        }
      }
      else {
        if (newPatch.outerSensor == SensorStatus.BREACH) {
          patchIcons.add(R.drawable.ic_detailed_plate_outer)
        }
        
        if(newPatch.innerSensorBreach) {
          patchIcons.add(R.drawable.ic_detailed_plate_inner_full)
        }
      }
      
      if (childCount > 1) {
        removeViews(1, childCount - 1)
      }
      
      patchIcons.forEach {
        val imageView = inflater.inflate(R.layout.view_imageview, this, false) as ImageView
        imageView.setImageResource(it)
        addView(imageView)
        imageView.visibleOrGoneFade(true)
      }
    }
  }
  
  fun setRawRotationalAngle(rawRotationalAngle: Int?) {
    rawRotationalAngle?.let {
      val angle = it.times(360)
        .div(128)
      val newAngle = UiUtils.calculatePatchRotation(angle)
      
      if (rotation != newAngle) {
        val animation = ObjectAnimator.ofFloat(rotation, newAngle)
        
        animation.addUpdateListener { valueAnimator ->
          rotation = valueAnimator.animatedValue as Float
        }
        
        animation.duration = 1500
        animation.start()
      }
    }
  }
  
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    
    if (isInEditMode) {
      setBackgroundResource(R.drawable.ic_detailed_plate_ok)
    }
  }
}