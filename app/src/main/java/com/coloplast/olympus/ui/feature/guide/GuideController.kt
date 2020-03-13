package com.coloplast.olympus.ui.feature.guide

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.BR
import com.coloplast.olympus.databinding.ControllerGuideBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guide.model.ArticleItem
import com.coloplast.olympus.ui.feature.guide.model.HeaderItem
import com.coloplast.olympus.ui.feature.guide.model.TextItem
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class GuideController : BaseController<ControllerGuideBinding>() {
  
  
  
  val items = mutableListOf<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
    .map(ArticleItem::class.java, BR.item, R.layout.item_article)
    .map(TextItem::class.java, BR.item, R.layout.item_guide_text)
   
  
  override fun attachVariables(viewBinding: ControllerGuideBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerGuideBinding) {
    items.add(HeaderItem(getString(R.string.guide_get_started).toString(), getString(R.string.guide_get_started_sub_title_1).toString()))
    items.add(ArticleItem(R.drawable.guide_get_started_1, getString(R.string.guide_get_started_1_text).toString(), getString(R.string.guide_get_started_sub_title_2).toString()))
    items.add(ArticleItem(R.drawable.guide_get_started_2, getString(R.string.guide_get_started_2_text).toString(), ""))
    items.add(ArticleItem(R.drawable.guide_get_started_3, getString(R.string.guide_get_started_3_text).toString(), ""))
    items.add(ArticleItem(R.drawable.guide_get_started_4, getString(R.string.guide_get_started_4_text).toString(), ""))
    items.add(ArticleItem(R.drawable.guide_get_started_5, getString(R.string.guide_get_started_5_text).toString(), getString(R.string.guide_get_started_sub_title_6).toString()))
    items.add(ArticleItem(R.drawable.guide_get_started_6, getString(R.string.guide_get_started_6_text).toString(), ""))
    items.add(ArticleItem(R.drawable.guide_get_started_7, getString(R.string.guide_get_started_7_text).toString(), ""))
    items.add(ArticleItem(R.drawable.guide_get_started_8, getString(R.string.guide_get_started_8_text).toString(), ""))
    items.add(ArticleItem(R.drawable.guide_get_started_9, "", getString(R.string.guide_get_started_sub_title_10).toString()))
    items.add(ArticleItem(R.drawable.guide_get_started_10, getString(R.string.guide_get_started_10_text).toString(), ""))
    items.add(ArticleItem(R.drawable.guide_get_started_11, getString(R.string.guide_get_started_11_text).toString(), getString(R.string.guide_get_started_sub_title_12).toString()))
    items.add(TextItem(getString(R.string.guide_get_started_12_text).toString()))
  }
  
  override fun getLayout() = R.layout.controller_guide
  
  companion object {
    private const val ROUTER_TAG = "get_started"
    
    fun routerTransaction() = RouterTransaction.with(GuideController()).tag(ROUTER_TAG)
  }
  
}