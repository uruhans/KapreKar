package com.coloplast.olympus.ui.feature.debug

import androidx.databinding.ObservableArrayList
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.coloplast.olympus.BR
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerLogsBinding
import com.coloplast.olympus.ui.common.BaseController
import com.coloplast.olympus.ui.feature.debug.model.LogFile
import com.coloplast.olympus.utils.DeviceUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

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