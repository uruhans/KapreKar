package com.coloplast.olympus.ui.common.extensions

import androidx.databinding.BindingAdapter
import com.coloplast.olympus.ui.common.widgets.PasswordView

object PasswordViewAdapters {

    @BindingAdapter("passwordCallback")
    @JvmStatic
    fun setPasswordCallback(passwordView: PasswordView, callbackFunction: (String) -> Unit) {
        passwordView.onPasswordChanged = callbackFunction
    }

    @BindingAdapter("passwordValidityCallback")
    @JvmStatic
    fun setPasswordValidityCallback(passwordView: PasswordView, callbackFunction: (Boolean) -> Unit) {
        passwordView.onValidityChanged = callbackFunction
    }

    @BindingAdapter("checkValidity")
    @JvmStatic
    fun shouldCheckValidity(passwordView: PasswordView, shouldCheckValidity: Boolean) {
        passwordView.shouldCheckValidity = shouldCheckValidity
    }

    @BindingAdapter("hint")
    @JvmStatic
    fun hint(passwordView: PasswordView, hint: String){
        passwordView.setHintText(hint)
    }

    @BindingAdapter("forceValidation")
    @JvmStatic
    fun forceValidation(passwordView: PasswordView, forceValidation: Boolean){
        if(forceValidation)
            with(passwordView){
                callback.invoke(password)
            }
    }
}