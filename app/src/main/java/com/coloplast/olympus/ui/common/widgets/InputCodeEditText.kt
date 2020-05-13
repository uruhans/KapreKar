package com.coloplast.olympus.ui.common.widgets

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import com.coloplast.olympus.databinding.ViewCodeInputBinding
import com.coloplast.olympus.utils.DeviceUtils


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 11/05/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class InputCodeEditText @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    interface Listener{
        fun onCodeInput(code: String)
    }

    lateinit var listener: Listener

    private val inflater = LayoutInflater.from(context)
    private val view = ViewCodeInputBinding.inflate(inflater, this, true).apply {
        view = this@InputCodeEditText

        this.input1.onFocusChangeListener = CustomOnFocusListener()
        this.input2.onFocusChangeListener = CustomOnFocusListener()
        this.input3.onFocusChangeListener = CustomOnFocusListener()
        this.input4.onFocusChangeListener = CustomOnFocusListener()

        this.input1.doAfterTextChanged { if(it?.isNotBlank() ?: false) input2.requestFocus() }
        this.input2.doAfterTextChanged { if(it?.isNotBlank() ?: false) input3.requestFocus() }
        this.input3.doAfterTextChanged { if(it?.isNotBlank() ?: false) input4.requestFocus() }
        this.input4.doAfterTextChanged { if(it?.isNotBlank() ?: false) callbackCode() }

        //Start
        input1.requestFocus()
        DeviceUtils.showSoftKeyboard(context)
    }

    private fun callbackCode(){
        DeviceUtils.closeSoftKeyboard(context)
        val code = "${view.input1.text}" +
                "${view.input2.text}" +
                "${view.input3.text}" +
                "${view.input4.text}"
        listener!!.onCodeInput(code)
    }

    inner class CustomOnFocusListener: OnFocusChangeListener{
        override fun onFocusChange(view: View, focused: Boolean) {
            if(focused) (view as EditText).text.clear()
        }
    }
}