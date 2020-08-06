package com.coloplast.olympus.ui.common.flow

import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.ui.common.models.flow.FlowFactoryModel
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implement the flowFactory to create a flow or wizard (a list of controllers in a specific order needed for a certain task)
 */
abstract class FlowFactory<V: FlowFactoryModel> {

  interface Listener<V: FlowFactoryModel> {
    /**
     * Tell the
     * implementer that this routerTransaction is the next to be shown on screen
     */
    fun next(routerTransaction: RouterTransaction)

    /**
     * In order to not add previous controller to the backstack (using a goto to go back in the flow)
     * we need to popControllers - use this method to do it properly
     *
     * @see getFlowChildRouter().popToTag()
     */
    fun popUntil(currentIndex: Int) { }
  
    /**
     * Tell the implementer that the flow is completed, and pass back the final result of data
     */
    fun onFlowCompleted(data: V)

    /**
     * Implement this method to receive all data on every step before moving on to the next step
     *
     * NOTICE: MUST RETURN TRUE IF OVERRIDDEN (AND YOU MUST MANUALLY CALL @see FlowFactory.incrementAndNext() IF YOU DO)
     *
     * ** this is used to do something with the data in between steps ie: call the server on a specific step before the end of the flow **
     */
    fun onStepCompleted(currentIndex: Int, data: V) : Boolean = false
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
   * Call this if you have implemented @see onStepCompleted()
   */
  fun incrementAndNext() {
    currentIndex.getAndIncrement()
    next()
  }

  /**
   * Determines where to go next and makes a callback to either
   * listener.onFlowCompleted (if no more steps are found in the flow) or
   * listener.next() - containing the next routerTransaction to display on screen
   */
  private fun next() {
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
   * Whenever a controller in the flow passes on a result, we parse the result
   * then make a callback to the flow controller with the data collected until now (only if implemented)
   */
  fun onResult(result: Any?) {
    parseResultFromController(currentIndex.get(), result)

    // call onStepCompleted (if implemented) - else go next()
    if(listener?.onStepCompleted(currentIndex.get(), getData()) == false) {
      incrementAndNext()
    }
  }
  
  /**
   * we need to call this when we go back
   */
  fun previous(popControllers: Boolean = false) {
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

    if(popControllers) {
      listener?.popUntil(currentIndex.get())
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
    // the incoming index is the one we want to see next time - so minus 1
    while(currentIndex.get() < index - 1) {
      skipNext()
    }
  }

  /**
   * skip the next step
   * call this after processing the result from the controller @see parseResultFromController()
   */
  fun skipNext() {
    // increment and add the current index to the skipped steps
    skippedSteps.add(currentIndex.incrementAndGet())
  }

  /**
   * Go to a specific index in the flow - can go back and forth
   * (with minor exception - currently cannot move back to a step that has been previously skipped)
   */
  fun goto(index: Int) {
    // if we are behind - skipUntil we reach the wanted index
    if(currentIndex.get() < index) {
      skipUntil(index)
    }
    else {
      // if we are ahead - we need to go back until we reach
      while(currentIndex.get() > index && currentIndex.get() >= 0) {
        // TODO - this could actually cause an issue, but unsure if we need it (we dont right now)
        // the case is that if you go 0,1,2,3 skipUntil(10), 11, goto(5)
        // what would happen is that it would go: 11,10,3,2,1,0 stop (never reach it because 5 was skipped)
        // in order to fix we should actually check after every previous to see if we are now behind the index,
        // and if we are we need to move forward again
        previous(true)
      }
    }
  }
}