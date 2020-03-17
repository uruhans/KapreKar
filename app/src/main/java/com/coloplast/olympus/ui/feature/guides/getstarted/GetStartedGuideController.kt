package com.coloplast.olympus.ui.feature.guides.getstarted

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuideGetStartedBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.models.*
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class GetStartedGuideController : BaseController<ControllerGuideGetStartedBinding>(), TopMenuItem.Listener, ButtonItem.Listener {
  
  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(TopMenuItem::class.java, BR.item, R.layout.item_top_menu_for_list)
    .map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
    .map(ArticleItem::class.java, BR.item, R.layout.item_article)
    .map(ButtonItem::class.java, BR.item, R.layout.item_button)
  
  override fun attachVariables(viewBinding: ControllerGuideGetStartedBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerGuideGetStartedBinding) {
    if (items.isNullOrEmpty()) {
      val list = listOf(
        TopMenuItem(this),
        HeaderItem(header = R.string.guide_get_started_title),
        ArticleItem(R.string.guide_get_started_sub_title_1, R.drawable.guide_get_started_1, R.string.guide_get_started_1_text),
        ArticleItem(R.string.guide_get_started_sub_title_2, R.drawable.guide_get_started_2, R.string.guide_get_started_2_text),
        ArticleItem(image = R.drawable.guide_get_started_3, description = R.string.guide_get_started_3_text),
        ArticleItem(image = R.drawable.guide_get_started_4, description = R.string.guide_get_started_4_text),
        ArticleItem(image = R.drawable.guide_get_started_5, description = R.string.guide_get_started_5_text),
        ArticleItem(R.string.guide_get_started_sub_title_6, R.drawable.guide_get_started_6, R.string.guide_get_started_6_text),
        ArticleItem(image = R.drawable.guide_get_started_7, description = R.string.guide_get_started_7_text),
        ArticleItem(image = R.drawable.guide_get_started_8, description = R.string.guide_get_started_8_text),
        ArticleItem(image = R.drawable.guide_get_started_9),
        ArticleItem(R.string.guide_get_started_sub_title_10, R.drawable.guide_get_started_10, R.string.guide_get_started_10_text),
        ArticleItem(image = R.drawable.guide_get_started_11, description = R.string.guide_get_started_11_text),
        ArticleItem(title = R.string.guide_get_started_sub_title_12, description = R.string.guide_get_started_12_text),
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
    if (buttonType == ButtonType.BACK) {
      handleBack()
    }
  }
  
  override fun getLayout() = R.layout.controller_guide_get_started
  
  companion object {
    private const val TAG_CONTROLLER = "get_started_guide_controller"
    
    fun routerTransaction() = RouterTransaction.with(GetStartedGuideController())
      .tag(TAG_CONTROLLER)
  }
}