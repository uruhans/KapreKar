package com.coloplast.olympus.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.coloplast.olympus.databinding.ItemTablayoutDotBinding

/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 13/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class CustomTabLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr){

    object CustomTabLayoutBindingAdapters{
        @JvmStatic @BindingAdapter(value = ["totalCount", "currentIndex"], requireAll = true)
        fun CustomTabLayout.drawDots(totalCount: Int, currentIndex: Int){
            orientation = HORIZONTAL

            val layoutInflater = LayoutInflater.from(context)
            for(i in 1 .. totalCount){
                ItemTablayoutDotBinding.inflate(layoutInflater, this, true).apply {
                    selected = (i == currentIndex)
                }
            }
        }
    }
}