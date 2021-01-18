package com.coloplast.olympus.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ViewEmailInputLayoutBinding
import com.coloplast.olympus.ui.common.extensions.hideSoftKeyboard
import timber.log.Timber
import kotlin.properties.Delegates


class EmailView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  @AttrRes defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), EmailValidator {

  // If error has already been shown. Validate email each text change.
  private var errorAlreadyShown = false

  //Validates the email. It invokes the validity callback and changes the background based on its state.
  internal val callback = { email: String ->
    validate(email).also { isValid ->
      if (shouldCheckValidity) {
        setEmailFieldBackground(isValid)
        onValidityChanged?.invoke(isValid)
        errorAlreadyShown = true
      }
    }
  }

  var email: String by Delegates.observable("") { _, _, new ->
    Timber.i(new)
    onEmailChanged?.invoke(new)
    if (errorAlreadyShown) {
      callback.invoke(new)
    }
  }

  val showInvalidEmailText = ObservableBoolean()
  val emailBackgroundDrawableResId = ObservableInt()

  val focusListener = ObservableField(OnFocusChangeListener(callback))

  //Set by the custom bindings at [EmailView].
  var onEmailChanged: ((String) -> Unit)? = null
  var onValidityChanged: ((Boolean) -> Unit)? = null
  var shouldCheckValidity = true

  // in order for us to also validate when the user pressed "back" to remove the keyboard
  // we need to validate the content again
  private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
    val insets = ViewCompat.getRootWindowInsets(this)
    val keyboardVisible = insets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
    if (!keyboardVisible) {
      onKeyboardActionDoneClicked()
    }
  }

  init {
    orientation = VERTICAL
    val binding = DataBindingUtil.inflate<ViewEmailInputLayoutBinding>(LayoutInflater.from(context), R.layout.view_email_input_layout, this, true)
    binding.view = this

    onFocusChangeListener = OnFocusChangeListener(callback)
    //Setting the default background drawable.
    emailBackgroundDrawableResId.set(R.drawable.edittext_background)

    viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
  }

  /**
   * Keyboard ACTION_DONE clicked callback.
   */
  fun onKeyboardActionDoneClicked() {
    callback.invoke(email)
  }

  private fun setEmailFieldBackground(isValid: Boolean) {
    when (isValid) {
      true -> R.drawable.edittext_background
      false -> R.drawable.edittext_background_error
    }.apply { emailBackgroundDrawableResId.set(this) }

    showInvalidEmailText.set(isValid.not())
  }


  //Focus listener that on focus lost, will hide the keyboard and will notify the callback.
  inner class OnFocusChangeListener(private val focusLostCallback: (String) -> Boolean) : View.OnFocusChangeListener {
    override fun onFocusChange(v: View, hasFocus: Boolean) {
      if (!v.hasFocus()) {
        v.hideSoftKeyboard()
        focusLostCallback.invoke(email)
      }
    }
  }

  override fun onDetachedFromWindow() {
    viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
    super.onDetachedFromWindow()
  }
}