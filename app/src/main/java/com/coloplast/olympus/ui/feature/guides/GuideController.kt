package com.coloplast.olympus.ui.feature.guides

import android.os.Bundle
import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuideBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.models.*
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class GuideController : BaseController<ControllerGuideBinding>, TopMenuItem.Listener, ButtonItem.Listener {
  
  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(TopMenuItem::class.java, BR.item, R.layout.item_top_menu_for_list)
    .map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
    .map(ArticleItem::class.java, BR.item, R.layout.item_article)
    .map(ButtonItem::class.java, BR.item, R.layout.item_button)
  
  private var guideType: GuideType = GuideType.UNKNOWN
  
  constructor() : super()
  constructor(args: Bundle) : super(args) {
    guideType = GuideType.fromOrdinal(args.getInt(BUNDLE_GUIDE_TYPE, -1))
  }
  
  override fun attachVariables(viewBinding: ControllerGuideBinding) {
    viewBinding.controller = this
  }
  
  override fun onViewBound(viewBinding: ControllerGuideBinding) {
    if (items.isNullOrEmpty()) {
      items.addAll(GuideContent.getGuideContent(guideType, this, this))
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
    else if (buttonType == ButtonType.ACTION) {
      when (guideType) {
        GuideType.APPLICATION_TIPS -> {
          router.pushController(
            routerTransaction(GuideType.APPLICATION)
              .pushChangeHandler(VerticalChangeHandler())
              .popChangeHandler(VerticalChangeHandler())
          )
        }
        
        GuideType.UNKNOWN,
        GuideType.APPLICATION,
        GuideType.CHARGING,
        GuideType.CLEANING,
        GuideType.CONNECTION,
        GuideType.GET_STARTED,
        GuideType.LEARN_MORE -> { /* nothing yet */
        }
      }
    }
  }
  
  override fun getLayout() = R.layout.controller_guide
  
  companion object {
    private const val TAG_CONTROLLER = "controller_guide"
    private const val BUNDLE_GUIDE_TYPE = "bundle_guide_type"
    
    fun routerTransaction(guideType: GuideType): RouterTransaction {
      Bundle().apply {
        putInt(BUNDLE_GUIDE_TYPE, guideType.ordinal)
      }
        .also {
          return RouterTransaction.with(GuideController(it))
            .tag(TAG_CONTROLLER)
        }
    }
  }
}