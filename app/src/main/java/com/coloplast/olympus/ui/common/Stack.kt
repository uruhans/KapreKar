package com.coloplast.olympus.ui.common

/**
 * a simple implementation of a stack with push, pop methods
 */
class Stack<T>(private var maxSize: Int) {

  private val items = mutableListOf<T>()

  fun push(item: T) {
    items.add(0, item)

    if(items.size > maxSize) {
      items.removeAt(items.size - 1)
    }
  }

  fun pop() : T? {
    return items.removeAt(0)
  }

  fun getLatestOrNull() : T? {
    return items.getOrNull(0)
  }

  fun getPrevious() : T? {
    return items.getOrNull(1)
  }

  fun setMaxSize(newMaxSize: Int) {
    this.maxSize = newMaxSize
  }
}