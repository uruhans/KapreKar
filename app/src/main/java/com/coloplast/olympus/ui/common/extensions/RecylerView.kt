package com.coloplast.olympus.ui.common.extensions

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 20/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */

@BindingAdapter("defaultDecorator")
fun RecyclerView.defaultDecorator(enable: Boolean){
  if(enable){
    for (i in 0..itemDecorationCount){
      removeItemDecorationAt(i)
    }
    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
  }
}