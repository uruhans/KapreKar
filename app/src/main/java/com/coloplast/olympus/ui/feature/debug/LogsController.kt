package com.coloplast.olympus.ui.feature.debug

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.BR
import com.coloplast.olympus.BuildConfig
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerLogsBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.debug.model.LogFile
import com.coloplast.olympus.utils.DeviceUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber
import java.io.File
import java.lang.Exception


class LogsController : BaseController<ControllerLogsBinding>(), LogFile.Listener {

  val items = ObservableArrayList<LogFile>()
  val itemBinding = ItemBinding.of<LogFile>(BR.item, R.layout.item_logfile)

  override fun attachVariables(viewBinding: ControllerLogsBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerLogsBinding) {
    items.clear()

    DeviceUtils.logDirectory().let { dir ->
      dir.listFiles()?.let { list ->
        items.addAll(list.map { LogFile(it.name, it.absolutePath, this) })
      }
    }
  }

  override fun onItemClicked(path: String) {
    router.pushController(
      LogFileController.routerTransaction(path)
        .popChangeHandler(HorizontalChangeHandler())
        .pushChangeHandler(HorizontalChangeHandler())
    )
  }

  override fun onEmailClicked(path: String) {
    try {
      val uri: Uri = FileProvider.getUriForFile(activity!!, BuildConfig.APPLICATION_ID + ".provider", File(path))
      val i = Intent(Intent.ACTION_SEND)
      i.type = "text/html"
      i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      i.putExtra(Intent.EXTRA_STREAM, uri)
      startActivity(Intent.createChooser(i, "Email:"))
    }
    catch(e: Exception) {
      Timber.e(e,"")
    }
  }

  override fun getLayout(): Int {
    return R.layout.controller_logs
  }

  companion object {
    private const val TAG_CONTROLLER = "logs_controller"

    fun routerTransaction(): RouterTransaction {
      return RouterTransaction.with(LogsController()).tag(TAG_CONTROLLER)
    }
  }

}