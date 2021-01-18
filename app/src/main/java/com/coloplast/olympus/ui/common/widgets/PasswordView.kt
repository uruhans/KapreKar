package com.coloplast.olympus.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ViewPasswordInputLayoutBinding
import com.coloplast.olympus.ui.common.extensions.hideSoftKeyboard
import timber.log.Timber
import kotlin.properties.Delegates


class PasswordView @JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             @AttrRes defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), PasswordValidator {

    // If error has already been shown. Validate password each text change.
    private var errorAlreadyShown = false

    //Validates the password. It invokes the validity callback and changes the background based on its state.
    internal val callback = { password: String ->
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

    // in order for us to also validate when the user pressed "back" to remove the keyboard
    // we need to validate the content again
    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val insets = ViewCompat.getRootWindowInsets(this)
        val keyboardVisible = insets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
        if (keyboardVisible) {
            onKeyboardActionDoneClicked()
        }
    }

    var binding :  ViewPasswordInputLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_password_input_layout, this, true)

    init {
        orientation = VERTICAL

        binding.view = this

        onFocusChangeListener = OnFocusChangeListener(callback)
        //Setting the default background drawable.
        passwordBackgroundDrawableResId.set(R.drawable.edittext_background)

        viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
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

    fun setHint(@StringRes resId: Int) {
        binding.securityCodeEditText.hint = context.getString(resId)
    }

    //Focus listener that on focus lost, will hide the keyboard and will notify the callback.
    inner class OnFocusChangeListener(private val focusLostCallback: (String) -> Boolean) : View.OnFocusChangeListener {
        override fun onFocusChange(v: View, hasFocus: Boolean) {
            if (!v.hasFocus()) {
                v.hideSoftKeyboard()
                focusLostCallback.invoke(password)
            }
        }
    }

    override fun onDetachedFromWindow() {
        viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        super.onDetachedFromWindow()
    }
}