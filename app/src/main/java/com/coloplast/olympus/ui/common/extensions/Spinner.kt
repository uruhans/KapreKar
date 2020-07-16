package com.coloplast.olympus.ui.common.extensions

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.coloplast.olympus.shared.types.TypeWithId


@BindingAdapter(value = ["selectedTypeWithId", "selectedValueAttrChanged"], requireAll = false)
fun Spinner.bindSpinnerData(
  newSelectedValue: TypeWithId?,
  newTextAttrChanged: InverseBindingListener
) {
  onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
      newTextAttrChanged.onChange()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}
  }
  if (newSelectedValue != null) {
    adapter.run {
      for (i in 0 until count){
        if(getItemAtPosition(i) == newSelectedValue){
          setSelection(i, true)
          break
        }
      }
    }
  }
}

@InverseBindingAdapter(attribute = "selectedTypeWithId", event = "selectedValueAttrChanged")
fun Spinner.getSelectedItem(): Any = selectedItem