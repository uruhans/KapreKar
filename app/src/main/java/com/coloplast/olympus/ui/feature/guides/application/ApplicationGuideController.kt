package com.coloplast.olympus.ui.feature.guides.application

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuideApplicationBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.models.*
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class ApplicationGuideController : BaseController<ControllerGuideApplicationBinding>(),
    TopMenuItem.Listener, ButtonItem.Listener {
  
  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(TopMenuItem::class.java, BR.item, R.layout.item_top_menu_for_list)
    .map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
    .map(ArticleItem::class.java, BR.item, R.layout.item_article)
    .map(ButtonItem::class.java, BR.item, R.layout.item_button)
  
  override fun attachVariables(viewBinding: ControllerGuideApplicationBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerGuideApplicationBinding) {
    if (items.isNullOrEmpty()) {
      val list = listOf(
        TopMenuItem(this),
        HeaderItem(R.string.guide_change_plate_title),
        ArticleItem(R.string.guide_change_plate_sub_title_1, R.drawable.guide_change_plate_1, R.string.guide_change_plate_body_1_text),
        ArticleItem(image = R.drawable.guide_change_plate_2, description = R.string.guide_change_plate_body_2_text),
        ArticleItem(image = R.drawable.guide_change_plate_3, description = R.string.guide_change_plate_body_3_text),
        ArticleItem(image = R.drawable.guide_change_plate_4, description = R.string.guide_change_plate_body_4_text),
        ArticleItem(R.string.guide_change_plate_sub_title_2, R.drawable.guide_change_plate_5, R.string.guide_change_plate_body_5_text),
        ArticleItem(image = R.drawable.guide_change_plate_6, description = R.string.guide_change_plate_body_6_text),
        ArticleItem(image = R.drawable.guide_change_plate_7, description = R.string.guide_change_plate_body_7_text),
        ArticleItem(image = R.drawable.guide_change_plate_8),
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
  
  override fun getLayout() = R.layout.controller_guide_application
  
  companion object {
    private const val TAG_CONTROLLER = "application_guide_controller"
    
    fun routerTransaction() = RouterTransaction.with(ApplicationGuideController())
      .tag(TAG_CONTROLLER)
  }
}