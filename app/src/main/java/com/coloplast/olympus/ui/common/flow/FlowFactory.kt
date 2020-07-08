package com.coloplast.olympus.ui.common.flow

import com.bluelinelabs.conductor.RouterTransaction
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implement the flowFactory to create a flow or wizard (a list of controllers in a specific order needed for a certain task)
 */
abstract class FlowFactory<V: FlowFactoryModel> {

  interface Listener<V: FlowFactoryModel> {
    /**
     * Tell the implementer that this routerTransaction is the next to be shown on screen
     */
    fun next(routerTransaction: RouterTransaction)
  
    /**
     * Tell the implementer that the flow is completed, and pass back the final result of data
     */
    fun onFlowCompleted(data: V)
  }
  
  private var initialStartIndex: Int = 0
  private val currentIndex: AtomicInteger = AtomicInteger(initialStartIndex)
  private val skippedSteps = mutableSetOf<Int>()
  private var listener: Listener<V>? = null

  /**
   * use this method to initialize any model objects or other things you need in the factory
   * NOTICE: this will be called whenever the flow restarts
   */
  abstract fun initialize()
  
  /**
   * returns the routerTransaction for the given index (step) that we are currently at
   */
  abstract fun getRouterTransaction(currentIndex: Int) : RouterTransaction?
  
  /**
   * the final data model to return
   */
  abstract fun getData() : V
  
  /**
   * parse any result from controllers in the flow
   */
  abstract fun parseResultFromController(currentIndex: Int, result: Any?)
  
  /**
   * set the listener to receive results from the factory 
   */
  fun setFlowFactoryListener(listener: Listener<V>) {
    this.listener = listener
  }
  
  /**
   * a method used to override the initial starting position of the flow
   * Maybe you want to start from position 3 instead of 0
   */
  fun overrideStartingPosition(index: Int) {
    initialStartIndex = index
    currentIndex.set(initialStartIndex)
  }
  
  /**
   * Determines where to go next and makes a callback to either
   * listener.onFlowCompleted (if no more steps are found in the flow) or
   * listener.next() - containing the next routerTransaction to display on screen
   */
  fun next() {
    // if this is the very first step, we call initialize
    if(currentIndex.get() == initialStartIndex) {
      initialize()
    }
    
    val routerTransaction = getRouterTransaction(currentIndex.get())
    
    if(routerTransaction == null) {
      listener?.onFlowCompleted(getData())
    }
    else {
      listener?.next(routerTransaction)
    }
  }
  
  /**
   * Whenever a controller in the flow passes on a result, we parse the result and go next()
   */
  fun onResult(result: Any?) {
    parseResultFromController(currentIndex.getAndIncrement(), result)
    next()
  }
  
  /**
   * we need to call this when we go back
   */
  fun previous() {
    currentIndex.decrementAndGet()

    skippedSteps.reversed().forEach { skipped ->
      // only go back if the current index matches the skipped index
      // this allows for "holes" in the skipping process (ie: 1,2,3,6,7,8)
      if (currentIndex.get() == skipped) {
        currentIndex.decrementAndGet()
        skippedSteps.remove(skipped)
      }
      else {
        return@forEach
      }
    }
  }
  
  /**
   * initiate a complete restart of the flow
   */
  fun restart() {
    currentIndex.set(initialStartIndex)
    skippedSteps.clear()
    next()
  }

  /**
   * skip steps that aren't needed
   * call this after processing the result from the controller @see parseResultFromController()
   */
  fun skipUntil(index: Int) {
    while(currentIndex.get() < index) {
      skipNext()
    }
  }

  /**
   * skip the next step
   * call this after processing the result from the controller @see parseResultFromController()
   */
  fun skipNext() {
    // add the current index to the skipped steps and increment
    skippedSteps.add(currentIndex.get())
    currentIndex.incrementAndGet()
  }
}