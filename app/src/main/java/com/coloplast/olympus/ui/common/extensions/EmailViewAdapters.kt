package com.coloplast.olympus.ui.common.extensions

import androidx.databinding.BindingAdapter
import com.coloplast.olympus.ui.common.widgets.EmailView

object EmailViewAdapters {

    @BindingAdapter("emailCallback")
    @JvmStatic
    fun setEmailCallback(emailView: EmailView, callbackFunction: (String) -> Unit) {
        emailView.onEmailChanged = callbackFunction
    }

    @BindingAdapter("emailValidityCallback")
    @JvmStatic
    fun setEmailValidityCallback(emailView: EmailView, callbackFunction: (Boolean) -> Unit) {
        emailView.onValidityChanged = callbackFunction
    }
    
    @BindingAdapter("checkValidity")
    @JvmStatic
    fun shouldCheckValidity(emailView: EmailView, shouldCheckValidity: Boolean) {
        emailView.shouldCheckValidity = shouldCheckValidity
    }
}