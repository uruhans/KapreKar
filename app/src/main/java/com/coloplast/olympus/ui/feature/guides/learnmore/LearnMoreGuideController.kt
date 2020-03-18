package com.coloplast.olympus.ui.feature.guides.learnmore

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuideLearnMoreBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.models.ButtonItem
import com.coloplast.olympus.ui.feature.guides.models.ButtonType
import com.coloplast.olympus.ui.feature.guides.models.HeaderItem
import com.coloplast.olympus.ui.feature.guides.models.TopMenuItem
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class LearnMoreGuideController : BaseController<ControllerGuideLearnMoreBinding>(), ButtonItem.Listener, TopMenuItem.Listener {
  
  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(TopMenuItem::class.java, BR.item, R.layout.item_top_menu_for_list)
    .map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
    .map(ButtonItem::class.java, BR.item, R.layout.item_button)
  
  override fun attachVariables(viewBinding: ControllerGuideLearnMoreBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerGuideLearnMoreBinding) {
    if (items.isNullOrEmpty()) {
      val list = listOf(
        TopMenuItem(this),
        HeaderItem(R.string.guide_learn_more_title, R.string.guide_learn_more_body),
        ButtonItem(R.string.guide_go_back, ButtonType.BACK, this)
      )
      items.addAll(list)
    }
  }
  
  override fun getLayout() = R.layout.controller_guide_learn_more
  
  override fun onButtonClicked(buttonType: ButtonType) {
    if (buttonType == ButtonType.BACK)
      handleBack()
  }
  
  override fun onBackClicked() {
    handleBack()
  }
  
  override fun onCloseClicked() {
    handleBack()
  }
  
  companion object {
    private const val TAG_CONTROLLER = "learn_more_guide_controller"
    fun routerTransaction() = RouterTransaction.with(LearnMoreGuideController())
      .tag(TAG_CONTROLLER)
  }
}