package com.coloplast.olympus.ui.feature.guides.charging

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuideChargingBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.models.*
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class ChargingGuideController : BaseController<ControllerGuideChargingBinding>(), TopMenuItem.Listener, ButtonItem.Listener {
  
  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(TopMenuItem::class.java, BR.item, R.layout.item_top_menu_for_list)
    .map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
    .map(ArticleItem::class.java, BR.item, R.layout.item_article)
    .map(ButtonItem::class.java, BR.item, R.layout.item_button)
  
  override fun attachVariables(viewBinding: ControllerGuideChargingBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerGuideChargingBinding) {
    if (items.isNullOrEmpty()) {
      val list = listOf(
        TopMenuItem(this),
        HeaderItem(R.string.guide_charging_title),
        ArticleItem(image = R.drawable.guide_get_started_1, description = R.string.guide_charging_1_text),
        ButtonItem(R.string.guide_go_back, ButtonType.BACK, this)
      )
      
      items.addAll(list)
    }
  }
  
  override fun onBackClicked() {
    handleBack()
  }
  
  override fun onCloseClicked() {
    handleBack()
  }
  
  override fun onButtonClicked(buttonType: ButtonType) {
    handleBack()
  }
  
  override fun getLayout() = R.layout.controller_guide_charging
  
  companion object {
    private const val TAG_CONTROLLER = "charging_guide_controller"
    
    fun routerTransaction() = RouterTransaction.with(ChargingGuideController())
      .tag(TAG_CONTROLLER)
  }
}