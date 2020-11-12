package com.coloplast.olympus.ui.common

/**
 * a simple implementation of a stack with push, pop methods
 */
class Stack<T>(private var maxSize: Int) {

  private val items = mutableListOf<T>()

  /**
   * Push an item on top of the stack
   */
  @Synchronized
  fun push(item: T) {
    synchronized(items) {
      items.add(0, item)
      possiblyRemoveItems()
    }
  }

  /**
   * Pop item from stack
   */
  @Synchronized
  fun pop() : T? {
    synchronized(items) {
      return items.removeAt(0)
    }
  }

  /**
   * Gets the latest item from the stack (but does not remove it)
   */
  @Synchronized
  fun peek() : T? {
    synchronized(items) {
      return items.getOrNull(0)
    }
  }

  /**
   * Gets the previous item from the stack - the one before the latest (but does not remove it)
   */
  @Synchronized
  fun peekPrevious() : T? {
    synchronized(items) {
      return items.getOrNull(1)
    }
  }

  @Synchronized
  fun peekPrevious(index: Int) : T? {
    synchronized(items) {
      return items.getOrNull(index)
    }
  }

  /**
   * Override the max size of the stack
   */
  @Synchronized
  fun setMaxSize(newMaxSize: Int) {
    this.maxSize = newMaxSize
    possiblyRemoveItems()
  }

  @Synchronized
  private fun possiblyRemoveItems() {
    synchronized(items) {
      while (items.size > maxSize) {
        items.removeAt(items.size - 1)
      }
    }
  }

  @Synchronized
  fun size() : Int {
    synchronized(items) {
      return items.size
    }
  }

  @Synchronized
  fun firstOrNull(predicate: (T) -> Boolean) : T? {
    synchronized(items) {
      return items.firstOrNull(predicate)
    }
  }
}