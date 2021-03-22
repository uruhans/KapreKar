package com.coloplast.olympus.ui.common.widgets

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.SpinnerBinding

class SpinnerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private lateinit var menuItems: List<CharSequence>
    private var title: String
    private var selectedIndex: Int = 0

    interface Listener {
        fun onItemIndexSelected(id: Int, index: Int)
    }

    lateinit var listener: Listener

    fun setMenuItems(items: List<String>) {
        menuItems = items
    }

    fun setTitle(title: String) {
        this.title = title
    }

    var textView = TextView(context)

    private val inflater = LayoutInflater.from(context)
    private val view = SpinnerBinding.inflate(inflater, this, true)
        .apply {
            view = this@SpinnerView
            textView = this.input
            textView.setOnClickListener(CustomOnClickListener())
        }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.SpinnerView).run {
            val refId = getResourceId(R.styleable.SpinnerView_selectableList, 0)
            if (refId != 0) {
                menuItems = context.resources.getStringArray(refId).toList()
            }

            if (hasValue(R.styleable.SpinnerView_prompt)) {
                title = getString(R.styleable.SpinnerView_prompt).toString()
            }
            else {
                title = ""
            }
            textView.setText(title)

            // Disable input.
            textView.maxLines = 1
            textView.inputType = InputType.TYPE_NULL
            textView.imeOptions = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            recycle()
        }
    }

    private fun showDialog() {
        menuItems.let {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(menuItems.toTypedArray()) { _, which ->
                    textView.setText(menuItems[which])
                    callbackSelectedItemIndex(which)
                }
                .show()
        }
    }

    private fun callbackSelectedItemIndex(index: Int) {
        listener.onItemIndexSelected(this.id, index)
    }

    inner class CustomOnClickListener : OnClickListener {
        override fun onClick(v: View?) {
            showDialog()
        }
    }
}
