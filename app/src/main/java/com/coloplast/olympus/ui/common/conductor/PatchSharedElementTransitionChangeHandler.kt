package com.coloplast.olympus.ui.common.conductor

import android.os.Bundle
import android.transition.*
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.changehandler.SharedElementTransitionChangeHandler

class PatchSharedElementTransitionChangeHandler(waitForTransitionNames: List<String> = listOf("Patch")) : SharedElementTransitionChangeHandler() {
  private val names = mutableListOf<String>()
  
  init {
    names.addAll(waitForTransitionNames)
  }
  
  override fun saveToBundle(bundle: Bundle) {
    bundle.putStringArray(KEY_WAIT_FOR_TRANSITION_NAMES, names.toTypedArray())
  }
  
  override fun restoreFromBundle(bundle: Bundle) {
    val savedNames: List<String> = bundle.getStringArray(KEY_WAIT_FOR_TRANSITION_NAMES)?.toList() ?: emptyList()
    names.addAll(savedNames)
  }
  
  override fun getExitTransition(container: ViewGroup, from: View?, to: View?, isPush: Boolean): Transition {
    return if (isPush) {
      Fade().setDuration(100)
    }
    else {
      Slide(Gravity.BOTTOM)
    }
  }
  
  override fun getSharedElementTransition(container: ViewGroup, from: View?, to: View?, isPush: Boolean): Transition {
    return TransitionSet()
      .addTransition(ChangeBounds())
      .addTransition(ChangeClipBounds())
      .addTransition(ChangeTransform())
      .addTransition(ChangeImageTransform())
      .setDuration(300)
  }
  
  override fun getEnterTransition(container: ViewGroup, from: View?, to: View?, isPush: Boolean): Transition {
    return if (isPush) {
      Slide(Gravity.BOTTOM)
    }
    else {
      Fade().setDuration(100)
    }
  }
  
  override fun configureSharedElements(container: ViewGroup, from: View?, to: View?, isPush: Boolean) {
    for (name in names) {
      addSharedElement(name)
    }
  }
  
  companion object {
    private const val KEY_WAIT_FOR_TRANSITION_NAMES = "PatchSharedElementTransitionChangeHandler.names"
  }
}