package com.coloplast.olympus.ui.common

import android.os.Bundle
import androidx.annotation.AnyRes
import androidx.annotation.StringRes
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerMediaGuideBinding
import com.coloplast.olympus.ui.common.flow.TopMenuVisibilityListener
import com.coloplast.olympus.ui.common.listeners.FlowListener


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 07/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 *
 * Guide base screen. Allows either a @Drawable image or a @Raw video
 */
open class MediaGuideController(args: Bundle) : BaseController<ControllerMediaGuideBinding>(args),
  TopMenuVisibilityListener {

  val screenName: String = args.getString(SCREEN_NAME)!!
  open val resourceId: Int = args.getInt(RESOURCE_ID)
  open val looping: Boolean = args.getBoolean(LOOPING)
  open val titleId: Int = args.getInt(TITLE_ID)
  open val descriptionId: Int = args.getInt(DESCRIPTION_ID)
  open val position: Int = args.getInt(POSITION)
  open val totalCount: Int = args.getInt(TOTAL_COUNT)
  open val showQuestionFab: Boolean = args.getBoolean(SHOW_QUESTION_FAB)
  open var nextEnabled: Boolean = args.getBoolean(ENABLE_NEXT)
  open var showBackButton: Boolean = args.getBoolean(SHOW_BACK_BUTTON)
  open var showCloseButton: Boolean = args.getBoolean(SHOW_CLOSE_BUTTON)
  open var showContinueButton: Boolean = args.getBoolean(SHOW_CONTINUE_BUTTON)

  interface Listener {
    fun onQuestionClicked(screen: String)
  }

  override fun attachVariables(viewBinding: ControllerMediaGuideBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerMediaGuideBinding) {
    //no-op
  }

  open fun onNextClicked() = getListener(FlowListener::class.java)!!.onNextClicked()

  open fun onQuestionClicked() = getListener(Listener::class.java)!!.onQuestionClicked(screenName)

  override fun showBackButton(): Boolean = showBackButton

  override fun showCloseButton(): Boolean = showCloseButton

  override fun getLayout() = R.layout.controller_media_guide

  companion object {
    const val SCREEN_NAME = "SCREEN_NAME"
    private const val RESOURCE_ID = "RESOURCE_ID"
    private const val LOOPING = "LOOPING"
    private const val TITLE_ID = "TITLE_ID"
    private const val DESCRIPTION_ID = "DESCRIPTION_ID"
    private const val POSITION = "POSITION"
    private const val TOTAL_COUNT = "TOTAL_COUNT"
    private const val SHOW_QUESTION_FAB = "SHOW_QUESTION_FAB"
    private const val SHOW_BACK_BUTTON = "SHOW_BACK_BUTTON"
    private const val SHOW_CLOSE_BUTTON = "SHOW_CLOSE_BUTTON"
    private const val ENABLE_NEXT = "ENABLE_NEXT"
    private const val SHOW_CONTINUE_BUTTON = "SHOW_CONTINUE_BUTTON"

    fun routerTransaction(
      @AnyRes resourceId: Int,
      looping: Boolean = true,
      @StringRes titleId: Int,
      @StringRes descriptionId: Int,
      position: Int = 0,
      totalCount: Int = 0,
      showQuestionFab: Boolean = true,
      showBackButton: Boolean = true,
      showCloseButton: Boolean = true,
      screenName: String = "MEDIA_CONTROLLER_GUIDE",
      enableNExt: Boolean = true,
      showContinueButton: Boolean = false
    ): RouterTransaction {
      val args = Bundle().apply {
        putInt(RESOURCE_ID, resourceId)
        putBoolean(LOOPING, looping)
        putInt(TITLE_ID, titleId)
        putInt(DESCRIPTION_ID, descriptionId)
        putInt(POSITION, position)
        putInt(TOTAL_COUNT, totalCount)
        putBoolean(SHOW_QUESTION_FAB, showQuestionFab)
        putBoolean(SHOW_BACK_BUTTON, showBackButton)
        putBoolean(SHOW_CLOSE_BUTTON, showCloseButton)
        putString(SCREEN_NAME, screenName)
        putBoolean(ENABLE_NEXT, enableNExt)
        putBoolean(SHOW_CONTINUE_BUTTON, showContinueButton)
      }
      return RouterTransaction.with(MediaGuideController(args))
        .tag(screenName)
    }
  }


}