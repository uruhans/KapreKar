package com.coloplast.olympus.ui.feature.guides.faq

import androidx.annotation.StringRes
import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerFaqBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.WebViewController
import com.coloplast.olympus.ui.feature.guides.models.AccordionItem
import me.tatarka.bindingcollectionadapter2.ItemBinding


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 21/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class FaqController: BaseController<ControllerFaqBinding>() {

  //TODO: This still have to be changed for the real content (that we don't have yet).
  val items = ObservableArrayList<AccordionItem>().apply {
    add(
      AccordionItem(
        questionId = R.string.guide_faq_question_1,
        answerId = R.string.guide_faq_answer_1,
        linkTextId = R.string.onboarding_dc5_link1_text,
        action = {
          openUrl(R.string.onboarding_dc5_link1)
        })
    )
    add(AccordionItem(
      questionId = R.string.guide_faq_question_1,
      answerId = R.string.guide_faq_answer_1
     ))
    add(AccordionItem(
      questionId = R.string.guide_faq_question_1,
      answerId = R.string.guide_faq_answer_1,
      linkTextId = R.string.onboarding_dc5_link2_text,
      action = {
        openUrl(R.string.onboarding_dc5_link2)
      }))
  }

  val itemBinding = ItemBinding.of<AccordionItem>(BR.item, R.layout.item_accordion)
    .bindExtra(BR.controller, this)


  override fun attachVariables(viewBinding: ControllerFaqBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerFaqBinding) {
    //noop
  }

  private fun openUrl(@StringRes urlId: Int){
    applicationContext?.getString(urlId)?.let { url ->
      getContentRouter().pushController(
        WebViewController.getRouterTransaction(url)
          .pushChangeHandler(VerticalChangeHandler())
          .popChangeHandler(VerticalChangeHandler())
      )
    }
  }

  override fun getLayout(): Int = R.layout.controller_faq

  companion object {
    const val TAG_CONTROLLER = "faq_controller"

    fun routerTransaction() = RouterTransaction.with(FaqController())
      .tag(TAG_CONTROLLER)
  }
}