package com.coloplast.olympus.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.coloplast.olympus.ui.common.models.Navigation
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import timber.log.Timber

abstract class BaseController<T : ViewDataBinding> : Controller, KoinComponent {
  
  protected val compositeDisposable = CompositeDisposable()
  
  constructor() : super()
  constructor(args: Bundle?) : super(args)
  
  private lateinit var viewBinding: T
  
  /**
   * will use [DataBindingUtil.inflate] to inflate a [ViewDataBinding] passed in by the generic <T>
   *
   * @return the view which will be added to the router
   */
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    // inflate the layout
    viewBinding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
    
    // attach any variables there might be
    attachVariables(viewBinding)
    
    // and execute pending bindings to set any default values observables may have
    viewBinding.executePendingBindings()
    
    onViewBound(viewBinding)
    return viewBinding.root
  }
  
  /**
   * attach any variables that you might have in your ViewDataBinding or leave empty
   */
  abstract fun attachVariables(viewBinding: T)
  
  /**
   * get the ViewDataBinding that was created during inflation
   */
  protected fun getBinding(): T {
    return viewBinding
  }
  
  /**
   * method is hit immediately after the binding has been created and the view has been inflated
   * do your stuff here
   */
  abstract fun onViewBound(viewBinding: T)
  
  /**
   * what layout to inflate - this layout must correspond to the BaseControllers<T> generic
   */
  @LayoutRes
  abstract fun getLayout(): Int
  
  /**
   * returns an existing ViewModel or creates it in this scope (application)
   * and will live until the scope is destroyed or process killed
   * @param modelClass the viewModel type to return
   */
  //protected fun <V : ViewModel> getViewModel(modelClass: Class<V>): V {
  //  return ViewModelProvider.AndroidViewModelFactory(activity!!.application).create(modelClass)
  //}
  
  /**
   * try to cast this controllers targetController into the clazz provided
   * @param clazz the type to try to cast the targetController to
   */
  open fun <V> getListener(clazz: Class<V>): V? {
    return getListener(targetController, clazz)
  }
  
  /**
   * try to cast the provided controller into the clazz provided
   * @param controller the controller to try to cast
   * @param clazz the type to try to cast the controller to
   */
  protected open fun <V> getListener(controller: Controller?, clazz: Class<V>): V? {
    if (controller != null) {
      if (clazz.isAssignableFrom(controller::class.java)) {
        return clazz.cast(controller)
      }
      else {
        Timber.e("$controller does not implement interface of type $clazz")
      }
    }
    else {
      Timber.d("Listener was null")
    }
    return null
  }
  
  /**
   * return the contentRouter used for "normal" navigation
   */
  protected fun getContentRouter(): Router {
    return Navigation.getContentRouter()!!
  }
  
  /**
   * override this to do custom unbinding
   */
  open fun onUnbind(viewBinding: T) {
    viewBinding.unbind()
  }
  
  override fun handleBack(): Boolean {
    if (getContentRouter().backstackSize > 1) {
      return getContentRouter().popCurrentController()
    }
    return getContentRouter().popToRoot()
  }
  
  /**
   * determines whether we need to show permission rationale or not
   * this will be passed to the PermissionHandler first (if the current class is implementing the
   * PermissionHandler interface)
   */
  override fun shouldShowRequestPermissionRationale(permission: String): Boolean {
    return getPermissionRequest()?.shouldShowRequestPermissionRationale(activity!!, permission)
      ?: super.shouldShowRequestPermissionRationale(permission)
  }
  
  /**
   * the permission request is received here and passed along to the PermissionHandler (if the current
   * class is implementing the PermissionHandler interface)
   */
  override fun onRequestPermissionsResult(
      requestCode: Int, permissions: Array<out String>, grantResults: IntArray
  ) {
    getPermissionRequest()?.onPermissionsResult(requestCode, permissions, grantResults)
  }
  
  
  /**
   * will try to convert this class to an instance of PermissionHandler
   */
  private fun getPermissionRequest(): PermissionHandler? {
    return this as PermissionHandler
  }
  
  /**
   * uses the regular getString method on the applicationContext
   * Will return null if applicationContext is not initialized
   */
  protected fun getString(@StringRes resId: Int): CharSequence? {
    return applicationContext?.getString(resId)
  }
  
  protected fun getString(@StringRes resId: Int, vararg formatArgs: Any): CharSequence? {
    return applicationContext?.getString(resId, *formatArgs)
  }
  
  /**
   * when view is being destroyed we need to unbind everything
   */
  override fun onDestroyView(view: View) {
    super.onDestroyView(view)
  
    compositeDisposable.clear()
    onUnbind(getBinding())
  }
}