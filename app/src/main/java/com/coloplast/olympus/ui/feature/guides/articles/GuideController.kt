package com.coloplast.olympus.ui.feature.guides.articles

import android.os.Bundle
import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerGuideBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.guides.articles.models.*
import com.coloplast.olympus.ui.feature.guides.flows.location.LocationGuideFlowController
import com.coloplast.olympus.ui.feature.guides.models.ButtonType
import com.coloplast.olympus.ui.feature.transmitters.TransmittersController
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class GuideController(args: Bundle) : BaseController<ControllerGuideBinding>(args) {

  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(HeaderItem::class.java, BR.item, R.layout.item_guide_header)
    .map(ArticleItem::class.java, BR.item, R.layout.item_article)
    .map(ButtonItem::class.java, BR.item, R.layout.item_button)

  private var guideType = GuideType.fromId(args.getInt(BUNDLE_GUIDE_TYPE))

  private val buttonCallback: (ButtonType) -> Unit = { buttonType ->
    when (buttonType) {
      ButtonType.BACK -> handleBack()
      ButtonType.ACTION,
      ButtonType.LINK -> {
        val routerTransaction = when (guideType) {
          GuideType.APPLICATION_TIPS -> routerTransaction(GuideType.APPLICATION)
          GuideType.CONNECTION -> LocationGuideFlowController.routerTransaction()
          GuideType.CLEANING -> routerTransaction(GuideType.DRYING)
          GuideType.DELETE_TRANSMITTER -> TransmittersController.routerTransaction()
          else -> throw IllegalArgumentException("This shouldn't happen")
        }
        getContentRouter().pushController(
          routerTransaction
            .pushChangeHandler(HorizontalChangeHandler())
            .popChangeHandler(HorizontalChangeHandler())
        )
      }
    }
  }

  override fun attachVariables(viewBinding: ControllerGuideBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerGuideBinding) {
    if (items.isNullOrEmpty()) {
      items.addAll(GuideContent.getGuideContent(guideType, buttonCallback))
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
          return RouterTransaction.with(
            GuideController(
              it
            )
          )
            .tag(TAG_CONTROLLER)
        }
    }
  }
}