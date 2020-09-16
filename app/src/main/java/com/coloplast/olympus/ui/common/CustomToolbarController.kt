package com.coloplast.olympus.ui.common

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import timber.log.Timber

abstract class CustomToolbarController<T : ViewDataBinding> : BaseController<T>, ToolbarSupportController.Listener {
  
  constructor() : super()
  constructor(args: Bundle?) : super(args)
  
  @CallSuper
  override fun onViewBound(viewBinding: T) {
    val toolbar: Toolbar? = viewBinding.root.findViewById(getToolbarId())
    
    if (toolbar != null) {
      initToolbar(toolbar)
    }
    else {
      throw NullPointerException("You are using CustomToolbarController, but I can't find the toolbar - maybe override getToolbar() to set a custom id?")
    }
  }
  
  private fun initToolbar(toolbar: Toolbar) {
    val backButton = toolbar.findViewById<ImageView>(R.id.button_back)
    val closeButton = toolbar.findViewById<ImageView>(R.id.button_close)
    
    if (displayNavigationIcon()) {
      backButton.setImageResource(getNavigationIconResId())
      backButton.setOnClickListener { onBackClicked() }
    }
    
    closeButton.setImageResource(R.drawable.main_sidemenu_close)
    closeButton.setOnClickListener { onCloseClicked() }
    
    toolbar.visibility = if (showToolbar()) View.VISIBLE else View.GONE
  }
  
  @CallSuper
  override fun attachVariables(viewBinding: T) {
    val success = viewBinding.setVariable(BR.toolbarSupport, ToolbarSupportController(listener = this))
    if (!success) {
      Timber.e("Tried to attach ToolbarSupportController to viewBinding, but was not successful")
    }
  }
  
  /**
   * show toolbar or not
   */
  open fun showToolbar(): Boolean {
    return true
  }
  
  /**
   * override this to change the default navigation icon
   * default: R.drawable.ic_arrow_back_black_24dp
   */
  @DrawableRes
  open fun getNavigationIconResId(): Int {
    return R.drawable.ic_arrow
  }
  
  /**
   * the default behavior is to "go back" - ie: router.popController(this)
   * Override this method to change that behavior and implement your own.
   */
  open fun onNavigationMenuItemClicked() {
    // at this point the activity should not be able to be null, as something would be *seriously* wrong
    // but lets crash and notify developer
    if (activity == null) {
      throw NullPointerException("activity is null - this should not be possible")
    }
    
    activity?.onBackPressed()
  }
  
  /**
   * whether or not to display the navigation icon
   * defaults to true
   */
  open fun displayNavigationIcon(): Boolean {
    return true
  }
  
  /**
   * override to change the default id of the toolbar used in the layout
   * default: R.id.toolbar
   */
  @IdRes
  open fun getToolbarId(): Int {
    return R.id.toolbar
  }
  
  override fun onBackClicked() {
    onNavigationMenuItemClicked()
  }
  
  override fun onCloseClicked() {
    handleBack()
  }
}