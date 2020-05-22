package com.coloplast.olympus.ui.common

/**
 * a simple implementation of a stack with push, pop methods
 */
class Stack<T>(private var maxSize: Int) {

  private val items = mutableListOf<T>()

  /**
   * Push an item on top of the stack
   */
  fun push(item: T) {
    items.add(0, item)

    if(items.size > maxSize) {
      items.removeAt(items.size - 1)
    }
  }

  /**
   * Pop item from stack
   */
  fun pop() : T? {
    return items.removeAt(0)
  }

  /**
   * Gets the latest item from the stack (but does not remove it)
   */
  fun getLatestOrNull() : T? {
    return items.getOrNull(0)
  }

  /**
   * Gets the previous item from the stack - the one before the latest (but does not remove it)
   */
  fun getPrevious() : T? {
    return items.getOrNull(1)
  }

  /**
   * Override the max size of the stack
   */
  fun setMaxSize(newMaxSize: Int) {
    this.maxSize = newMaxSize
  }
}