package com.coloplast.olympus.ui.common.extensions

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

// taken from https://medium.com/@rmirabelle/close-hide-the-soft-keyboard-in-android-db1da22b09d2
fun Activity.hideSoftKeyboard() {
  val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
  //Find the currently focused view, so we can grab the correct window token from it.

  var view = currentFocus;

  //f no view currently has focus, create a new one, just so we can grab a window token from it
  if (view == null) {
    view = View(this)
  }

  inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0);
}

