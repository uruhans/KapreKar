package com.coloplast.olympus.ui.feature.guides.connection

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuideConnectionBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.models.ButtonItem
import com.coloplast.olympus.ui.feature.guides.models.ButtonType
import com.coloplast.olympus.ui.feature.guides.models.HeaderItem
import com.coloplast.olympus.ui.feature.guides.models.TopMenuItem
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class ConnectionGuideController : BaseController<ControllerGuideConnectionBinding>(), ButtonItem.Listener, TopMenuItem.Listener {
  
  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(TopMenuItem::class.java, BR.item, R.layout.item_top_menu_for_list)
    .map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
    .map(ButtonItem::class.java, BR.item, R.layout.item_button)
  
  override fun attachVariables(viewBinding: ControllerGuideConnectionBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerGuideConnectionBinding) {
    if (items.isNullOrEmpty()) {
      val list = listOf(
        TopMenuItem(this),
        HeaderItem(R.string.guide_ble_connection_title, R.string.guide_ble_connection_body),
        ButtonItem(R.string.guide_go_back, ButtonType.BACK, this)
      )
      items.addAll(list)
    }
  }
  
  override fun getLayout() = R.layout.controller_guide_connection
  
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
    private const val TAG_CONTROLLER = "connection_guide_controller"
    
    fun routerTransaction() = RouterTransaction.with(ConnectionGuideController())
      .tag(TAG_CONTROLLER)
  }
}