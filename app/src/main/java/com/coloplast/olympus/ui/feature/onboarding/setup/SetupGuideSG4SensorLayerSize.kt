package com.coloplast.olympus.ui.feature.onboarding.setup

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStartupGuide4Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.feature.onboarding.setup.model.NumberedCard
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class SetupGuideSG4 : BaseController<ControllerStartupGuide4Binding>(), TopMenuVisibilityListener {

  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(NumberedCard::class.java, BR.item, R.layout.item_numbered_cardview)

  override fun attachVariables(viewBinding: ControllerStartupGuide4Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStartupGuide4Binding) {
    if (items.isNullOrEmpty()) {
      val list = listOf(
        NumberedCard(NumberedCard.Order.First, R.string.onboarding_chapter_intro_checklist_item_1, enabled = true, showCheckmark = true),
        NumberedCard(textRes = R.string.onboarding_chapter_intro_checklist_item_2, enabled = true, showCheckmark = true),
        NumberedCard(textRes = R.string.onboarding_chapter_intro_checklist_item_3, enabled = true),
        NumberedCard(NumberedCard.Order.Last, R.string.onboarding_chapter_intro_checklist_item_4)
      )

      items.addAll(list)
    }
  }

  override fun showBackButton(): Boolean = false

  override fun showCloseButton(): Boolean = false

  fun onNextClicked() {
    getListener(FlowListener::class.java)?.onNextClicked()
  }

  override fun getLayout(): Int {
    return R.layout.controller_startup_guide_4
  }

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_sg_4_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SetupGuideSG4())
        .tag(TAG_CONTROLLER)
    }
  }
}