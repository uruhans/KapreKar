package com.coloplast.olympus.ui.common.extensions

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.coloplast.olympus.R


@BindingAdapter("onSubmit")
fun EditText.onSubmit(action: () -> Unit) {
  imeOptions = EditorInfo.IME_ACTION_DONE;

  setOnEditorActionListener { _, actionId, _ ->
    if (actionId == EditorInfo.IME_ACTION_DONE) {
      hideSoftKeyboard()
      action.invoke()
    }
    true
  }
}

@BindingAdapter("errorBackground")
fun EditText.errorBackground(validField: Boolean){
  if(validField){
    setBackgroundResource(R.drawable.edittext_background)
  } else {
    setBackgroundResource(R.drawable.edittext_background_error)
  }
}