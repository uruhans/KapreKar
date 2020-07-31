package com.coloplast.olympus.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ViewPasswordInputLayoutBinding
import com.coloplast.olympus.ui.common.extensions.hideKeyboard
import timber.log.Timber
import kotlin.properties.Delegates


class PasswordView @JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             @AttrRes defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), PasswordValidator {

    // If error has already been shown. Validate password each text change.
    private var errorAlreadyShown = false

    //Validates the password. It invokes the validity callback and changes the background based on its state.
    private val callback = { password: String ->
        validate(password).also { isValid ->
            if (shouldCheckValidity) {
                setPasswordFieldBackground(isValid)
                onValidityChanged?.invoke(isValid)
                errorAlreadyShown = true
            }
        }
    }

    var password: String by Delegates.observable("") { _, _, new ->
        Timber.i(new)
        onPasswordChanged?.invoke(new)
        if(errorAlreadyShown){
            callback.invoke(new)
        }
    }

    val showInvalidPasswordText = ObservableBoolean()
    val passwordBackgroundDrawableResId = ObservableInt()

    val focusListener = ObservableField(OnFocusChangeListener(callback))

    //Set by the custom bindings at [PasswordView].
    var onPasswordChanged: ((String) -> Unit)? = null
    var onValidityChanged: ((Boolean) -> Unit)? = null
    var shouldCheckValidity = true

    init {
        orientation = VERTICAL
        val binding = DataBindingUtil.inflate<ViewPasswordInputLayoutBinding>(LayoutInflater.from(context), R.layout.view_password_input_layout, this, true)
        binding.view = this

        onFocusChangeListener = OnFocusChangeListener(callback)
        //Setting the default background drawable.
        passwordBackgroundDrawableResId.set(R.drawable.edittext_background)
    }

    /**
     * Keyboard ACTION_DONE clicked callback.
     */
    fun onKeyboardActionDoneClicked() {
        callback.invoke(password)
    }

    private fun setPasswordFieldBackground(isValid: Boolean) {
        when (isValid) {
            true -> R.drawable.edittext_background
            false -> R.drawable.edittext_background_error
        }.apply { passwordBackgroundDrawableResId.set(this) }

        showInvalidPasswordText.set(isValid.not())
    }

    //Focus listener that on focus lost, will hide the keyboard and will notify the callback.
    inner class OnFocusChangeListener(private val focusLostCallback: (String) -> Boolean) : View.OnFocusChangeListener {
        override fun onFocusChange(v: View, hasFocus: Boolean) {
            if (!v.hasFocus()) {
                v.hideKeyboard()
                focusLostCallback.invoke(password)
            }
        }
    }
}