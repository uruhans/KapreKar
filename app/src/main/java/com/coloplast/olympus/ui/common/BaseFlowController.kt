package com.coloplast.olympus.ui.common

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerFlowBinding
import com.coloplast.olympus.ui.common.listeners.FlowListener

/**
 * Base class to support and hold controllers contained within a "step-based" flow (or wizard)
 * It shows no UI, but is merely a container that holds other controllers and navigates between them
 */
@Suppress("PrivatePropertyName")
abstract class BaseFlowController : BaseController<ControllerFlowBinding>, FlowListener, ToolbarSupportController.Listener {
  
  private val ROUTER_TAG = "BaseFlowController.childRouter"
  
  val title = ObservableField("")
  val showToolbar = ObservableBoolean(false)
  
  val progress = ObservableInt()
  val progressMax = ObservableInt()
  val showProgress = ObservableBoolean(false)
  val showSubmitButton = ObservableBoolean(false)
  
  constructor() : super()
  constructor(args: Bundle?) : super(args)
  
  override fun attachVariables(viewBinding: ControllerFlowBinding) {
    viewBinding.toolbarSupport = ToolbarSupportController(listener = this)
    viewBinding.controller = this
  }
  
  @CallSuper
  override fun onViewBound(viewBinding: ControllerFlowBinding) {
    startFlow()
    
    showSubmitButton.addOnPropertyChangedCallback(onShowSubmitButtonChanged)
//    initializeToolbarMargins(viewBinding)
  }
  
  override fun onBackClicked() {
    handleBack()
  }
  
  override fun onCloseClicked() {
    closeFlow()
  }
  
  /**
   * initialize toolbar margins depending on whether or not the submit button is displayed in the toolbar
   */
//  private fun initializeToolbarMargins(viewBinding: ControllerFlowBinding) {
//    val params = viewBinding.title.layoutParams as LinearLayout.LayoutParams
//    params.marginEnd = if (showSubmitButton.get()) {
//      0
//    }
//    else {
//      viewBinding.title.context.resources.getDimensionPixelSize(
//      R.dimen.toolbar_margin_with_navigation_icon_2
//      )
//    }
//
//    viewBinding.title.layoutParams = params
//
//    val iconParams = viewBinding.navigationIcon.layoutParams as LinearLayout.LayoutParams
//    iconParams.marginEnd = 0
//    viewBinding.navigationIcon.layoutParams = iconParams
//  }
  
  private fun startFlow() {
    if (childRouters.isEmpty()) {
      startFlow(getChildRouter(getBinding().flowControllerContainer, ROUTER_TAG))
    }
    else {
      restartFlow(getChildRouter(getBinding().flowControllerContainer, ROUTER_TAG))
    }
  }
  
  /**
   * return the router contained within this controller
   * this is the router that holds and controls the steps in the flow
   */
  protected fun getFlowChildRouter(): Router {
    return getChildRouter(getBinding().flowControllerContainer, ROUTER_TAG)
  }
  
  /**
   * helper method to push a new controller onto the flow controllers router
   * default changehandlers are [HorizontalChangeHandler]
   */
  protected fun pushController(
      routerTransaction: RouterTransaction,
      overrideDefaultChangeHandlers: Boolean = false
  ) {
    routerTransaction.controller()
        .targetController = this
    
    if(!getFlowChildRouter().hasRootController()) {
      getFlowChildRouter().setRoot(routerTransaction)
    }
    else {
      if (!overrideDefaultChangeHandlers) {
        routerTransaction.pushChangeHandler(HorizontalChangeHandler())
          .popChangeHandler(HorizontalChangeHandler())
      }
  
      getFlowChildRouter().pushController(routerTransaction)
    }
  }
  
  /**
   * listen for changes to the showSubmitButton observable, as this requires the toolbar to be initialized again
   * due to changes in the margins
   */
  private val onShowSubmitButtonChanged = object : Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
      //initializeToolbarMargins(getBinding())
    }
  }
  
  /**
   * define what should happen on initial launch - usually you would init the first controller,
   * and set it as root on the flowChildRouter
   */
  abstract fun startFlow(flowChildRouter: Router)
  
  /**
   * if for some reason we want to restart the flow, we already have a backstack of controllers,
   * so you probably want to pop to the first controller again (and possibly clear any user data that might exist)
   */
  open fun restartFlow(flowChildRouter: Router) {
    // default implementation so users aren't forced to implement
  }
  
  override fun getLayout(): Int {
    return R.layout.controller_flow
  }
  
  /**
   * override this to determine what should happen when you press the navigation icon (ie: top left button)
   */
  open fun onNavigationButtonClicked() {
    handleBack()
  }
  
  /**
   * override this to determine what should happen when you press the submit button (ie: top right button)
   */
  open fun onSubmitClicked() {}
  
  /**
   * use this to set the maxProgress - it will multiply by 100 to make the animations smoother
   */
  protected fun setMaxProgress(maxProgress: Int) {
    this.progressMax.set(maxProgress.times(100))
  }
  
  /**
   * use this to set the current progress - it will multiply by 100 to make the animations smoother
   */
  protected fun setCurrentProgress(currentProgress: Int) {
    this.progress.set(currentProgress.times(100))
  }
  
  /**
   * close the flow - this will close the flow no matter where we are in our wizard
   */
  fun closeFlow() {
    router.popController(this)
  }
  
  override fun onUnbind(viewBinding: ControllerFlowBinding) {
    showSubmitButton.removeOnPropertyChangedCallback(onShowSubmitButtonChanged)
    super.onUnbind(viewBinding)
  }
  
  /**
   * default implementation of FlowListener.onPreviousClicked
   */
  override fun onPreviousClicked() {
    handleBack()
  }
  
  /**
   * default implementation of FlowListener.onCancelFlow
   */
  override fun onCancelFlow() {
    closeFlow()
  }
  
  /**
   * default implementation of handleBack
   */
  @CallSuper
  override fun handleBack(): Boolean {
    if(getFlowChildRouter().backstackSize > 1) {
      getFlowChildRouter().popCurrentController()
    }
    else {
      closeFlow()
    }
    
    return true
  }
}