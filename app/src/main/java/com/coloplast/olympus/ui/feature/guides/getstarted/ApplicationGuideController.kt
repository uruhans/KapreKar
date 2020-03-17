package com.coloplast.olympus.ui.feature.guides.getstarted

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerApplicationGuideBinding
import com.coloplast.olympus.ui.common.BaseToolbarController
import com.coloplast.olympus.ui.feature.guides.getstarted.model.ArticleItem
import com.coloplast.olympus.ui.feature.guides.getstarted.model.HeaderItem
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class ApplicationGuideController : BaseToolbarController<ControllerApplicationGuideBinding>() {
  
  val items = mutableListOf<Any>()
  val itemBinding: OnItemBindClass<Any> =
    OnItemBindClass<Any>().map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
      .map(ArticleItem::class.java, BR.item, R.layout.item_article)
  
  override fun attachVariables(viewBinding: ControllerApplicationGuideBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerApplicationGuideBinding) {
    super.onViewBound(viewBinding)
    if (items.isNullOrEmpty()) {
      items.add(
        HeaderItem(
          R.string.guide_change_plate_title
        )
      )
      items.add(
        ArticleItem(
          R.string.guide_change_plate_sub_title_1,
          R.drawable.guide_change_plate_1,
          R.string.guide_change_plate_body_1_text
        )
      )
      items.add(
        ArticleItem(
          image = R.drawable.guide_change_plate_2,
          description = R.string.guide_change_plate_body_2_text
        )
      )
      items.add(
        ArticleItem(
          image = R.drawable.guide_change_plate_3,
          description = R.string.guide_change_plate_body_3_text
        )
      )
      items.add(
        ArticleItem(
          image = R.drawable.guide_change_plate_4,
          description = R.string.guide_change_plate_body_4_text
        )
      )
      items.add(
        ArticleItem(
          R.string.guide_change_plate_sub_title_2,
          R.drawable.guide_change_plate_5,
          R.string.guide_change_plate_body_5_text
        )
      )
      
      items.add(
        ArticleItem(
          image = R.drawable.guide_change_plate_6,
          description = R.string.guide_change_plate_body_6_text
        )
      )
      items.add(
        ArticleItem(
          image = R.drawable.guide_change_plate_7,
          description = R.string.guide_change_plate_body_7_text
        )
      )
      items.add(
        ArticleItem(
          image = R.drawable.guide_change_plate_8
        )
      )
    }
  }
  
  override fun getTitle(): Any {
    return ""
  }
  
  override fun displayNavigationIcon(): Boolean {
    return true
  }
  
  override fun getLayout() = R.layout.controller_application_guide
  
  companion object {
    private const val TAG_CONTROLLER = "application_guide_controller"
    
    fun routerTransaction() = RouterTransaction.with(ApplicationGuideController())
      .tag(
        TAG_CONTROLLER
      )
  }
  
  
}