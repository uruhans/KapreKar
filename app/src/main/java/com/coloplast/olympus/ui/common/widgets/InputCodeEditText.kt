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

        this.input1.addTextChangedListener(CustomTextChangedListener { input2.requestFocus() })
        this.input2.addTextChangedListener(CustomTextChangedListener { input3.requestFocus() })
        this.input3.addTextChangedListener(CustomTextChangedListener { input4.requestFocus() })
        this.input4.addTextChangedListener(CustomTextChangedListener { callbackCode() })

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

    inner class CustomTextChangedListener(private val action: () -> Unit): TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun afterTextChanged(text: Editable) {
            if(text.isNotBlank()) action.invoke()
        }
    }

}