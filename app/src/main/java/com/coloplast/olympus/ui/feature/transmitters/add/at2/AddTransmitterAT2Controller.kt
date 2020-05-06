package com.coloplast.olympus.ui.feature.transmitters.add.at2

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerAddTransmitterAt2Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.transmitters.add.models.ChecklistItem
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class AddTransmitterAT2Controller : BaseController<ControllerAddTransmitterAt2Binding>() {
  
  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(ChecklistItem::class.java, BR.item, R.layout.item_guide_checklist)
  
  override fun attachVariables(viewBinding: ControllerAddTransmitterAt2Binding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerAddTransmitterAt2Binding) {
    if (items.isNullOrEmpty()) {
      val list = listOf(
        ChecklistItem(R.string.guide_at2_item1),
        ChecklistItem(R.string.guide_at2_item2),
        ChecklistItem(R.string.guide_at2_item3),
        ChecklistItem(R.string.guide_at2_item4),
        ChecklistItem(R.string.guide_at2_item5)
      )
    
      items.addAll(list)
    }
  }
  
  override fun getLayout() = R.layout.controller_add_transmitter_at_2
  
  companion object {
    private const val TAG_CONTROLLER = "guide_transmitters_at2_controller"
    
    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(AddTransmitterAT2Controller())
        .tag(TAG_CONTROLLER)
    }
  }
}