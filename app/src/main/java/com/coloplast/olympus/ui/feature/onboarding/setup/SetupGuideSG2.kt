package com.coloplast.olympus.ui.feature.onboarding.setup

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStartupGuide2Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.feature.onboarding.setup.model.NumberedCard
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class SetupGuideSG2 : BaseController<ControllerStartupGuide2Binding>() {

  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(NumberedCard::class.java, BR.item, R.layout.item_numbered_cardview)

  override fun attachVariables(viewBinding: ControllerStartupGuide2Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStartupGuide2Binding) {
    if (items.isNullOrEmpty()) {
      val list = listOf(
        NumberedCard(NumberedCard.Order.First, R.string.onboarding_chapter_intro_checklist_item_1, enabled = true),
        NumberedCard(textRes = R.string.onboarding_chapter_intro_checklist_item_2),
        NumberedCard(textRes = R.string.onboarding_chapter_intro_checklist_item_3),
        NumberedCard(NumberedCard.Order.Last, R.string.onboarding_chapter_intro_checklist_item_4)
      )

      items.addAll(list)
    }
  }

  fun onNextClicked() {
    getListener(FlowListener::class.java)?.onNextClicked()
  }

  override fun getLayout(): Int {
    return R.layout.controller_startup_guide_2
  }

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_sg_2"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SetupGuideSG2())
        .tag(TAG_CONTROLLER)
    }
  }
}