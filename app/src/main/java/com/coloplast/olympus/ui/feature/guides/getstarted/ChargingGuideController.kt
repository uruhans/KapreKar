package com.coloplast.olympus.ui.feature.guides.getstarted

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerChargingGuideBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.getstarted.model.ArticleItem
import com.coloplast.olympus.ui.feature.guides.getstarted.model.HeaderItem
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class ChargingGuideController:BaseController<ControllerChargingGuideBinding>(){
  
  val items = mutableListOf<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
    .map(ArticleItem::class.java, BR.item, R.layout.item_article)
  
  override fun attachVariables(viewBinding: ControllerChargingGuideBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerChargingGuideBinding) {
    if(items.isNullOrEmpty()) {
      items.add(
        HeaderItem(
          R.string.guide_charging_title, showSubHeader = false)
        )
      items.add(
        ArticleItem(
          R.drawable.guide_get_started_1,
          R.string.guide_charging_1_text
        )
      )
    }
  }
  
  override fun getLayout() = R.layout.controller_charging_guide
  companion object {
    private const val ROUTER_TAG = "charging_guide_controller"
    
    fun routerTransaction() = RouterTransaction.with(ChargingGuideController()).tag(
      ROUTER_TAG
    )
  }
  
}