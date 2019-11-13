package com.coloplast.olympus.ui.common.models

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.coloplast.olympus.R

object Navigation {
  private val routerMap: LinkedHashMap<Int, Router> = LinkedHashMap()
  
  fun getOverlayRouter() = routerMap[R.id.content_overlay_controller]
  fun getContentRouter() = routerMap[R.id.main_navigation_controller_container]
  
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