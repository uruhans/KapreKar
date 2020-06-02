package com.coloplast.olympus.ui.common.widgets

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import com.coloplast.olympus.R
import com.coloplast.olympus.bluetooth.businesslayer.enums.SensorStatus
import com.coloplast.olympus.bluetooth.businesslayer.models.Patch
import com.coloplast.olympus.ui.common.extensions.tintDrawable
import com.coloplast.olympus.ui.common.extensions.visibleOrGoneFade
import com.coloplast.olympus.ui.common.listeners.AnimatorListenerImpl
import com.coloplast.olympus.utils.UiUtils
import com.coloplast.olympus.utils.UiUtils.dpToPx

class PatchView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
  
  interface Listener {
    fun onPatchClicked()
  }
  
  private val inflater = LayoutInflater.from(context)
  private val background = inflater.inflate(R.layout.view_imageview, this, false) as ImageView
  private var previousPatchState: Patch? = null
  
  private var isDetailView = false
  private var isInError = ObservableBoolean(false)
  private var rawRotationalAngle: Int? = null
  private var listener: Listener? = null
  
  private var patchSize: Int = 0
  
  private val errorStateChanged = object : Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
      if (isInError.get()) {
        clearTopImages()
        background.tintDrawable(R.color.patch_grey)
        rotateBasedOnAngle(0, forceRotation = true)
      }
      else {
        background.clearColorFilter()
        rotateBasedOnAngle(rawRotationalAngle, forceRotation = true)
      }
    }
  }
  
  init {
    initialize()
  }

  private fun initialize(sizeDp: Int = 0) {
    removeAllViews()
    background.setImageResource(R.drawable.ic_detailed_plate_ok)
    setSize(background, sizeDp)

    addView(background)
    background.visibility = View.VISIBLE

    isInError.addOnPropertyChangedCallback(errorStateChanged)
    clipChildren = false
    clipToPadding = false
  }

  fun setSize(sizeDp: Int) {
    patchSize = sizeDp
    initialize(patchSize)
  }
  
  fun isDetailView(isDetailView: Boolean = false) {
    this.isDetailView = isDetailView
  }
  
  fun setError(error: Boolean?) {
    error?.let {
      isInError.set(it)
    }
  }
  
  fun setListener(listener: Listener) {
    this.listener = listener
    
    setOnClickListener {
      this.listener?.onPatchClicked()
    }
  }
  
  fun setPatch(patch: Patch?) {
    if (isInError.get()) {
      return
    }
    
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
      
      var animatePulse = PulseAnim.NONE
      
      if (isDetailView) {
        if (newPatch.outerSensor == SensorStatus.OK) {
          if (newPatch.innerSensor1 == SensorStatus.BREACH) {
            patchIcons.add(R.drawable.ic_detailed_plate_inner_nw)
          }

          if (newPatch.innerSensor2 == SensorStatus.BREACH) {
            patchIcons.add(R.drawable.ic_detailed_plate_inner_s)
          }

          if (newPatch.innerSensor3 == SensorStatus.BREACH) {
            patchIcons.add(R.drawable.ic_detailed_plate_inner_ne)
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
              patchIcons.add(R.drawable.ic_detailed_plate_outer_inner_s)
            }

            if (newPatch.innerSensor3 == SensorStatus.BREACH) {
              patchIcons.add(R.drawable.ic_detailed_plate_outer_inner_ne)
            }
          }
        }
      }
      else {
        if (newPatch.outerSensor == SensorStatus.BREACH) {
          patchIcons.add(R.drawable.ic_detailed_plate_outer)
          animatePulse = PulseAnim.OUTER
        }
        
        if (newPatch.innerSensorBreach) {
          patchIcons.add(R.drawable.ic_detailed_plate_inner_full)
          if (animatePulse == PulseAnim.NONE) {
            animatePulse = PulseAnim.INNER
          }
        }
      }
      
      clearTopImages()
      
      patchIcons.forEach {
        val imageView = inflater.inflate(R.layout.view_imageview, this, false) as ImageView
        imageView.apply {
          setImageResource(it)
          addView(this)

          setSize(this, patchSize)
          visibleOrGoneFade(true)
        }
      }
      
      addPulseAnimation(animatePulse)
    }
  }
  
  private fun setSize(imageView: ImageView, sizeDp: Int) {
    if(isDetailView && sizeDp > 0) {
      val layoutParams = imageView.layoutParams
      layoutParams.height = sizeDp.dpToPx().toInt()
      layoutParams.width = sizeDp.dpToPx().toInt()
      imageView.layoutParams = layoutParams
    }
  }
  
  private fun clearTopImages() {
    if (childCount > 1) {
      removeViews(1, childCount - 1)
    }
  }
  
  fun setRawRotationalAngle(rawRotationalAngle: Int?) {
    rotateBasedOnAngle(rawRotationalAngle)
  }
  
  private fun rotateBasedOnAngle(rawRotationalAngle: Int?, forceRotation: Boolean = false) {
    if (isInError.get() && !forceRotation) {
      return
    }
    
    rawRotationalAngle?.let {
      if (!isInError.get()) {
        this.rawRotationalAngle = it
      }
      
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
  
  private fun addPulseAnimation(pulseAnim: PulseAnim) {
    if (pulseAnim == PulseAnim.NONE) {
      return
    }
    
    val imageView = inflater.inflate(R.layout.view_imageview, this, false) as ImageView
    imageView.apply {
      addView(imageView, 1)
    }
    
    val endScale = if (pulseAnim == PulseAnim.OUTER) {
      imageView.setImageResource(R.drawable.ic_detailed_plate_outer)
      1.2f
    }
    else {
      imageView.setImageResource(R.drawable.ic_detailed_plate_inner_full)
      1.5f
    }
    
    val animation = AnimatorSet()
    val alphaAnim = ObjectAnimator.ofFloat(imageView, View.ALPHA, 0f, .7f, 0f)
    val scaleAnimY = ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 1f, endScale)
    val scaleAnimX = ObjectAnimator.ofFloat(imageView, View.SCALE_X, 1f, endScale)
    
    animation.duration = 900
    animation.startDelay = 700
    animation.addListener(object : AnimatorListenerImpl {
      override fun onAnimationEnd(animation: Animator?) {
        removeView(imageView)
      }
      
      override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
        imageView.visibility = View.VISIBLE
      }
    })
    
    animation.playTogether(alphaAnim, scaleAnimX, scaleAnimY)
    animation.start()
  }
  
  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)
    
    if (isInEditMode) {
      setBackgroundResource(R.drawable.ic_detailed_plate_ok)
    }
  }
  
  enum class PulseAnim {
    NONE,
    OUTER,
    INNER
  }
}