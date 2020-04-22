package com.coloplast.olympus.ui.feature.transmitters.guides.at2

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerTransmitterGuideAt2Binding
import com.coloplast.olympus.ui.common.BaseController
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class GuideTransmitterAT2Controller : BaseController<ControllerTransmitterGuideAt2Binding>() {
  
  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(ChecklistItem::class.java, BR.item, R.layout.item_guide_checklist)
  
  override fun attachVariables(viewBinding: ControllerTransmitterGuideAt2Binding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerTransmitterGuideAt2Binding) {
    if (items.isNullOrEmpty()) {
      val list = listOf(
        ChecklistItem(R.string.guide_AT2_item1),
        ChecklistItem(R.string.guide_AT2_item2),
        ChecklistItem(R.string.guide_AT2_item3),
        ChecklistItem(R.string.guide_AT2_item4),
        ChecklistItem(R.string.guide_AT2_item5)
      )
    
      items.addAll(list)
    }
  }
  
  override fun getLayout() = R.layout.controller_transmitter_guide_at_2
  
  companion object {
    private const val TAG_CONTROLLER = "guide_transmitters_at2_controller"
    
    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(GuideTransmitterAT2Controller())
        .tag(TAG_CONTROLLER)
    }
  }
}