package com.coloplast.olympus.ui.feature.transmitters

import android.os.Bundle
import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerTransmittersBinding
import com.coloplast.olympus.ui.common.CustomToolbarController
import com.coloplast.olympus.ui.feature.transmitters.models.SectionDivider
import com.coloplast.olympus.ui.feature.transmitters.models.SmallHeaderItem
import com.coloplast.olympus.ui.feature.transmitters.models.TransmitterItem
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class TransmittersController(args: Bundle? = null) : CustomToolbarController<ControllerTransmittersBinding>(args) {
  
  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(SmallHeaderItem::class.java, BR.item, R.layout.item_small_header)
    .map(SectionDivider::class.java, BR.item, R.layout.item_recyclerview_section_divider)
    .map(TransmitterItem::class.java, BR.item, R.layout.item_transmitter)
  
  override fun attachVariables(viewBinding: ControllerTransmittersBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerTransmittersBinding) {
    super.onViewBound(viewBinding)
    
    val list = listOf(
      SmallHeaderItem(R.string.guide_wiping_title),
      TransmitterItem(),
      SectionDivider(),
      SmallHeaderItem(R.string.guide_wiping_title),
      TransmitterItem()
    )
    
    items.addAll(list)
  }
  
  override fun getNavigationIconResId(): Int {
    return R.drawable.ic_arrow
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_transmitters
  }
  
  companion object {
    private const val TAG_CONTROLLER = "transmitters_controller"
    
    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(TransmittersController())
        .tag(TAG_CONTROLLER)
    }
  }
}