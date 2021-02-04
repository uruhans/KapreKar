package com.coloplast.olympus.ui.feature.debug

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerLogfileBinding
import com.coloplast.olympus.ui.common.BaseController
import java.io.*
import java.lang.StringBuilder

class LogFileController(args: Bundle) : BaseController<ControllerLogfileBinding>(args) {

  private val filename = args.getString(BUNDLE_FILENAME)

  val fileContent = ObservableField<String>()

  override fun attachVariables(viewBinding: ControllerLogfileBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerLogfileBinding) {
    val builder = StringBuilder()

    val reader = BufferedReader(InputStreamReader(File(filename!!).inputStream(), "UTF-8"));

    var mLine: String? = reader.readLine()

    while(mLine != null) {
      builder.append(mLine)
      mLine = reader.readLine()
    }

    reader.let {
      reader.close()
    }

    fileContent.set(builder.toString())

    viewBinding.scrollView.post {
      viewBinding.scrollView.fullScroll(View.FOCUS_DOWN)
    }
  }

  override fun getLayout(): Int {
    return R.layout.controller_logfile
  }

  companion object {
    private const val TAG_CONTROLLER = "logs_controller"

    private const val BUNDLE_FILENAME = "BUNDLE_FILENAME"

    fun routerTransaction(filename: String): RouterTransaction {
      with(Bundle()) {
        putString(BUNDLE_FILENAME, filename)
        return RouterTransaction.with(LogFileController(this)).tag(TAG_CONTROLLER)
      }
    }
  }
}