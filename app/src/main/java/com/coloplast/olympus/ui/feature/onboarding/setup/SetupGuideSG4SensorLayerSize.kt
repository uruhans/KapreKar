package com.coloplast.olympus.ui.feature.onboarding.setup

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableInt
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerSensorLayerSelectorBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.feature.onboarding.setup.model.NumberedCard
import com.coloplast.olympus.ui.feature.onboarding.setup.model.SensorLayerSizeType
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class SetupGuideSG4SensorLayerSize : BaseController<ControllerSensorLayerSelectorBinding>(), TopMenuVisibilityListener {

  val selectedSize = ObservableInt(SensorLayerSizeType.UNKNOWN.ordinal)

  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(NumberedCard::class.java, BR.item, R.layout.item_numbered_cardview)

  override fun attachVariables(viewBinding: ControllerSensorLayerSelectorBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerSensorLayerSelectorBinding) {

  }

  fun onSensorLayerClicked(sensorLayerSizeType: SensorLayerSizeType) {
    selectedSize.set(sensorLayerSizeType.ordinal)
  }

  override fun showBackButton(): Boolean = true

  override fun showCloseButton(): Boolean = false

  fun onNextClicked() {
    getListener(FlowListener::class.java)?.onNextClicked()
  }

  override fun getLayout(): Int {
    return R.layout.controller_sensor_layer_selector
  }

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_sg_4_sensor_layer_size_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SetupGuideSG4SensorLayerSize())
        .tag(TAG_CONTROLLER)
    }
  }
}
