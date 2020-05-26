package com.coloplast.olympus.ui.common.extensions

import java.util.*

fun <T> Stack<T>.peekPrevious() : T? {
  return getOrNull(1)
}