package com.coloplast.olympus.ui.feature.guides.getstarted

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGetStartedGuideBinding
import com.coloplast.olympus.ui.common.BaseToolbarController
import com.coloplast.olympus.ui.feature.guides.getstarted.model.ArticleItem
import com.coloplast.olympus.ui.feature.guides.getstarted.model.HeaderItem
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class GetStartedGuideController : BaseToolbarController<ControllerGetStartedGuideBinding>() {
  
  val items = mutableListOf<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
    .map(ArticleItem::class.java, BR.item, R.layout.item_article)
   
  
  override fun attachVariables(viewBinding: ControllerGetStartedGuideBinding) {
    super.attachVariables(viewBinding)
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerGetStartedGuideBinding) {
    super.onViewBound(viewBinding)
    
    if(items.isNullOrEmpty()) {
      items.add(
        HeaderItem(
          R.string.guide_get_started,
          R.string.guide_get_started_sub_title_1
        )
      )
      items.add(
        ArticleItem(
          R.drawable.guide_get_started_1,
          R.string.guide_get_started_1_text,
          R.string.guide_get_started_sub_title_2
        )
      )
      items.add(
        ArticleItem(
          R.drawable.guide_get_started_2,  R.string.guide_get_started_2_text
        )
      )
      items.add(
        ArticleItem(
          R.drawable.guide_get_started_3, R.string.guide_get_started_3_text
        )
      )
      items.add(
        ArticleItem(
          R.drawable.guide_get_started_4, R.string.guide_get_started_4_text
        )
      )
      items.add(
        ArticleItem(
          R.drawable.guide_get_started_5,
          R.string.guide_get_started_5_text,
          R.string.guide_get_started_sub_title_6
        )
      )
      items.add(
        ArticleItem(
          R.drawable.guide_get_started_6, R.string.guide_get_started_6_text
        )
      )
      items.add(
        ArticleItem(
          R.drawable.guide_get_started_7, R.string.guide_get_started_7_text
        )
      )
      items.add(
        ArticleItem(
          R.drawable.guide_get_started_8, R.string.guide_get_started_8_text
        )
      )
      items.add(
        ArticleItem(
          R.drawable.guide_get_started_9, R.string.guide_get_started_sub_title_10
        )
      )
      items.add(
        ArticleItem(
          R.drawable.guide_get_started_10, R.string.guide_get_started_10_text
        )
      )
      items.add(
        ArticleItem(
          R.drawable.guide_get_started_11,
          R.string.guide_get_started_11_text,
          R.string.guide_get_started_sub_title_12
        )
      )
      items.add(
        ArticleItem(description = R.string.guide_get_started_12_text)
      )
    }
  }
  
  override fun getTitle(): Any {
    return ""
  }
  
  override fun displayNavigationIcon(): Boolean {
    return true
  }
  
  override fun getLayout() = R.layout.controller_get_started_guide
  
  companion object {
    private const val TAG_CONTROLLER = "get_started_guide_controller"
    
    fun routerTransaction() = RouterTransaction.with(GetStartedGuideController()).tag(TAG_CONTROLLER)
  }
}