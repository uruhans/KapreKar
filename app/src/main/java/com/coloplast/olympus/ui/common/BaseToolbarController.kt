package com.coloplast.olympus.ui.common

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.utils.UiUtils
import timber.log.Timber

abstract class BaseToolbarController<T : ViewDataBinding> : BaseController<T> {
  
  constructor() : super()
  constructor(args: Bundle?) : super(args)
  
  @CallSuper
  override fun onViewBound(viewBinding: T) {
    val toolbar: Toolbar? = viewBinding.root.findViewById(getToolbarId())
    
    if (toolbar != null) {
      initToolbar(toolbar)
    }
    else {
      Timber.e("You are using BaseToolbarController, but I can't find the toolbar - maybe override getToolbar() to set a custom id?")
    }
    
    toolbar!! // this will crash the app if the toolbar cannot be found (this is on purpose)
  }
  
  private fun initToolbar(toolbar: Toolbar) {
    // this is the default marginEnd if we have no menuItems
    var marginEnd = resources!!.getDimensionPixelSize(R.dimen.toolbar_margin_with_navigation_icon)
    
    // we could have a case where we want a toolbar with a navigation icon, but not
    // necessarily want our own menu items
    if (getMenuId() != UiUtils.INVALID_INT) {
      toolbar.inflateMenu(getMenuId())
      
      afterMenuInflated(toolbar.menu)
      
      toolbar.setOnMenuItemClickListener { menuItem: MenuItem? ->
        return@setOnMenuItemClickListener onMenuItemClicked(menuItem!!)
      }
      
      marginEnd = resources!!.getDimensionPixelSize(R.dimen.spacing_larger)
    }
    
    if (displayNavigationIcon() && getNavigationIconResId() != 0) {
      // initialize the navigation icon and set click listener
      toolbar.setNavigationIcon(getNavigationIconResId())
      toolbar.navigationIcon?.setTint(
        ContextCompat.getColor(
          activity!!.applicationContext, R.color.toolbar_icon_tint
        )
      )
      
      toolbar.setNavigationOnClickListener { onNavigationMenuItemClicked() }
      
      (toolbar.findViewById<LinearLayout>(R.id.toolbar_content_layout).layoutParams as Toolbar.LayoutParams).marginEnd =
        marginEnd
    }
    
    toolbar.visibility = if (showToolbar()) View.VISIBLE else View.GONE
  }
  
  @CallSuper
  override fun attachVariables(viewBinding: T) {
    val success = viewBinding.setVariable(BR.toolbarSupport, ToolbarSupportController(getTitle()))
    if (!success) {
      Timber.e("Tried to attach ToolbarSupportController to viewBinding, but was not successful")
    }
  }
  
  /**
   * the title that should be used on the toolbar
   */
  abstract fun getTitle(): Any
  
  /**
   * override the menuId to inflate a menu into the toolbar
   */
  @MenuRes
  open fun getMenuId(): Int {
    return UiUtils.INVALID_INT
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
    return R.drawable.ic_arrow_back_black_24dp
  }
  
  /**
   * override this to receive callbacks when a menuItem is clicked
   *
   * @param menuItem the menuItem that was clicked
   */
  open fun onMenuItemClicked(menuItem: MenuItem): Boolean {
    return false
  }
  
  /**
   * this is called after the menu has been inflated
   * use it to manipulate the menu after the fact
   */
  open fun afterMenuInflated(menu: Menu) {}
  
  /**
   * the default behavior is to "go back" - ie: router.popController(this)
   * Override this method to change that behavior and implement your own.
   */
  open fun onNavigationMenuItemClicked() {
    activity!!.onBackPressed()
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
}