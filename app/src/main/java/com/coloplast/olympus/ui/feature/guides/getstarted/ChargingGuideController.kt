package com.coloplast.olympus.ui.feature.guides.getstarted

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerChargingGuideBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.BaseToolbarController
import com.coloplast.olympus.ui.feature.guides.getstarted.model.ArticleItem
import com.coloplast.olympus.ui.feature.guides.getstarted.model.HeaderItem
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class ChargingGuideController:BaseToolbarController<ControllerChargingGuideBinding>(){
  
  val items = mutableListOf<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
    .map(ArticleItem::class.java, BR.item, R.layout.item_article)
  
  override fun attachVariables(viewBinding: ControllerChargingGuideBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerChargingGuideBinding) {
    super.onViewBound(viewBinding)
    if(items.isNullOrEmpty()) {
      items.add(
        HeaderItem(
          R.string.guide_charging_title)
        )
      items.add(
        ArticleItem(
          image = R.drawable.guide_get_started_1,
          description = R.string.guide_charging_1_text
        )
      )
    }
  }
  override fun displayNavigationIcon(): Boolean {
    return true
  }
  override fun getLayout() = R.layout.controller_charging_guide
  
  companion object {
    private const val TAG_CONTROLLER = "charging_guide_controller"
    
    fun routerTransaction() = RouterTransaction.with(ChargingGuideController()).tag(
      TAG_CONTROLLER
    )
  }
  
  override fun getTitle(): Any {
    return ""
  }
  
}