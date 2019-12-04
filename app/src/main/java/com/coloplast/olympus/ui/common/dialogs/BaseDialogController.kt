package com.coloplast.olympus.ui.common.dialogs

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.common.models.Navigation
import timber.log.Timber

@Suppress("PrivatePropertyName")
abstract class BaseDialogController<T : ViewDataBinding>(args: Bundle?) : BaseController<T>(args) {
  
  interface Listener {
    fun onPositiveButtonClicked(requestCode: Int) {
      // default implementation so users aren't forced to implement
    }
    
    fun onNeutralButtonClicked(requestCode: Int) {
      // default implementation so users aren't forced to implement
    }
    
    fun onNegativeButtonClicked(requestCode: Int) {
      // default implementation so users aren't forced to implement
    }
    
    fun onDialogDismissed(requestCode: Int) {
      // default implementation so users aren't forced to implement
    }
  }
  
  private val TAG = "BaseDialogController"
  private val navigation = Navigation
  
  fun show() {
    val routerTransaction = RouterTransaction.with(this)
        .popChangeHandler(FadeChangeHandler())
        .pushChangeHandler(FadeChangeHandler())
    
    show(navigation.getOverlayRouter()!!, routerTransaction)
  }
  
  fun show(overlayRouter: Router, routerTransaction: RouterTransaction) {
    overlayRouter.setRoot(routerTransaction.tag(TAG))
  }
  
  fun dismissDialog(withCallback: Boolean = true) {
    val router = navigation.getOverlayRouter()!!
    
    if (router.getControllerWithTag(TAG) != null) {
      val shouldPopLastView = router.backstackSize == 1
      if (shouldPopLastView) {
        router.setPopsLastView(true)
      }
      
      router.popController(this)
      
      if (shouldPopLastView) {
        router.setPopsLastView(false)
      }
  
      if(withCallback) {
        onDismiss()
      }
    } else {
      Timber.w("Tried to dismiss, but dialog TAG was not found")
    }
  }
  
  abstract fun onDismiss()
  
  override fun <V> getListener(clazz: Class<V>): V? {
    val controller = navigation.getContentRouter()!!.getControllerWithInstanceId(
      args.getString(BUNDLE_TARGET_INSTANCE_ID)!!
    )
    return super.getListener(controller, clazz)
  }
  
  override fun handleBack(): Boolean {
    return true
  }
  
  companion object {
    const val BUNDLE_TARGET_INSTANCE_ID = "bundle_target_instance_id"
  }
}