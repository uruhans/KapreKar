package com.coloplast.olympus.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.*
import com.coloplast.olympus.databinding.ViewSensorSelectorBinding
import com.coloplast.olympus.ui.feature.settings.models.Sensors


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 10/06/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class SensorSelectorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

  val allSensorsEnabled = ObservableBoolean(true)

  private val layoutInflater =
    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
  private val viewBinding = ViewSensorSelectorBinding.inflate(layoutInflater, this, true).apply {
    this.view = this@SensorSelectorView
  }

  fun onOptionClicked(sensors: Sensors){
    allSensorsEnabled.set(sensors == Sensors.ALL)
  }

  @InverseBindingMethods(InverseBindingMethod(type = SensorSelectorView::class, attribute = "allSensorsSelected", method = "getAllSensorsSelected"))
  object SensorSelectorBindingAdapter{
    @BindingAdapter("allSensorsSelected")
    @JvmStatic fun setAllSensorsSelected(view: SensorSelectorView, newState: Boolean) {
      if(view.allSensorsEnabled.get() != newState){
        view.allSensorsEnabled.set(newState)
      }
    }

    @InverseBindingAdapter(attribute = "allSensorsSelected")
    @JvmStatic fun getAllSensorsSelected(view: SensorSelectorView): Boolean {
      return view.allSensorsEnabled.get()
    }

    @BindingAdapter("allSensorsSelectedAttrChanged")
    @JvmStatic fun setListeners(view: SensorSelectorView, attrChange: InverseBindingListener?){
      view.allSensorsEnabled.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
          attrChange?.onChange()
        }
      })
    }
  }
}