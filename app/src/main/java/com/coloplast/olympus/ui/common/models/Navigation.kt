package com.coloplast.olympus.ui.common.models

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.coloplast.olympus.R
import io.reactivex.Maybe

object Navigation {
  private val routerMap: LinkedHashMap<Int, Router> = LinkedHashMap()
  
  fun getOverlayRouter() = routerMap[R.id.content_overlay_controller]
  fun getContentRouter() = routerMap[R.id.main_navigation_controller_container]
  
  inline fun <reified T> locateControllerOfType(): Maybe<T> {
    return Maybe.create { emitter ->
      val controller: T? = getContentRouter()?.backstack.orEmpty()
        .map { it.controller }
        .filterIsInstance(T::class.java)
        .first()
    
      if (controller != null) {
        emitter.onSuccess(controller)
      }
      else {
        emitter.onError(NullPointerException())
      }
    }
  }
  
  class Builder {
    private val routerContainers: ArrayList<Int> = ArrayList()
    
    fun addRouter(@IdRes containerId: Int): Builder {
      routerContainers.add(containerId)
      return this
    }
    
    fun build(activity: Activity, args: Bundle?): Navigation {
      routerContainers.forEach { containerId: Int ->
        val container = activity.findViewById<ViewGroup>(containerId)
        val router = Conductor.attachRouter(activity, container, args)
        routerMap[containerId] = router
      }
      
      return Navigation
    }
  }
}