package com.coloplast.olympus.ui.feature.guides.cleaning

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuideCleaningBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.models.*
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class CleaningGuideController : BaseController<ControllerGuideCleaningBinding>(), TopMenuItem.Listener, ButtonItem.Listener {
  
  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(TopMenuItem::class.java, BR.item, R.layout.item_top_menu_for_list)
    .map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
    .map(ArticleItem::class.java, BR.item, R.layout.item_article)
    .map(ButtonItem::class.java, BR.item, R.layout.item_button)
  
  override fun attachVariables(viewBinding: ControllerGuideCleaningBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerGuideCleaningBinding) {
    if (items.isNullOrEmpty()) {
      val list = listOf(
        TopMenuItem(this),
        HeaderItem(R.string.guide_cleaning_title),
        ArticleItem(description = R.string.guide_cleaning_1_text),
        ArticleItem(R.string.guide_cleaning_sub_title_2, R.drawable.guide_cleaning_2, R.string.guide_cleaning_2_text),
        ArticleItem(R.string.guide_cleaning_sub_title_3, R.drawable.guide_cleaning_3, R.string.guide_cleaning_3_text),
        ArticleItem(title = R.string.guide_cleaning_sub_title_4, description = R.string.guide_cleaning_4_text),
        ArticleItem(title = R.string.guide_cleaning_sub_title_5, description = R.string.guide_cleaning_5_text),
        ArticleItem(title = R.string.guide_wiping_title, description = R.string.guide_wiping_1_text),
        ArticleItem(title = R.string.guide_wiping_sub_title_2, description = R.string.guide_wiping_2_text),
        ArticleItem(R.string.guide_wiping_sub_title_3, R.drawable.guide_wiping_1, R.string.guide_wiping_3_text),
        ArticleItem(title = R.string.guide_wiping_sub_title_4, description = R.string.guide_wiping_4_text),
        ArticleItem(title = R.string.guide_wiping_sub_title_5, description = R.string.guide_wiping_5_text),
        ArticleItem(R.string.guide_wiping_sub_title_6, R.drawable.guide_wiping_2, R.string.guide_wiping_6_text),
        ButtonItem(R.string.guide_go_back, ButtonType.BACK, this)
      )
      items.addAll(list)
    }
  }
  
  override fun getLayout() = R.layout.controller_guide_cleaning
  
  override fun onBackClicked() {
    handleBack()
  }
  
  override fun onCloseClicked() {
    handleBack()
  }
  
  override fun onButtonClicked(buttonType: ButtonType) {
    if (buttonType == ButtonType.BACK)
      handleBack()
  }
  
  companion object {
    private const val TAG_CONTROLLER = "cleaning_guide_controller"
    
    fun routerTransaction() = RouterTransaction.with(CleaningGuideController())
      .tag(TAG_CONTROLLER)
  }
  
  
}