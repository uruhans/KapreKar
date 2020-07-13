package com.coloplast.olympus.ui.feature.onboarding.setup.intro

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerStartupGuideIntro1Binding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.listeners.FlowListener
import com.coloplast.olympus.ui.feature.transmitters.add.models.ChecklistItem
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass

class SetupGuideIntroSGI1 : BaseController<ControllerStartupGuideIntro1Binding>() {

  val items = ObservableArrayList<Any>()
  val itemBinding: OnItemBindClass<Any> = OnItemBindClass<Any>()
    .map(ChecklistItem::class.java, BR.item, R.layout.item_guide_checklist)

  override fun attachVariables(viewBinding: ControllerStartupGuideIntro1Binding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerStartupGuideIntro1Binding) {
    if (items.isNullOrEmpty()) {
      val list = listOf(
        ChecklistItem(R.string.onboarding_chapter_intro_bullet_item_1),
        ChecklistItem(R.string.onboarding_chapter_intro_bullet_item_2),
        ChecklistItem(R.string.onboarding_chapter_intro_bullet_item_3),
        ChecklistItem(R.string.onboarding_chapter_intro_bullet_item_4)
      )

      items.addAll(list)
    }
  }

  fun onNextClicked() {
    getListener(FlowListener::class.java)?.onNextClicked()
  }

  override fun getLayout(): Int {
    return R.layout.controller_startup_guide_intro_1
  }

  companion object {
    private const val TAG_CONTROLLER = "setup_guide_intro_sgi_1"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(SetupGuideIntroSGI1())
        .tag(TAG_CONTROLLER)
    }
  }
}