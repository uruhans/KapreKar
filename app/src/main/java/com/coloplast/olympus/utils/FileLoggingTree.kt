package com.coloplast.olympus.utils

import android.os.Environment
import android.util.Log
import timber.log.Timber.DebugTree
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class FileLoggingTree(private val loggingEnabled: Boolean = true) : DebugTree() {
  override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    if(!loggingEnabled) {
      return
    }

    if(priority != Log.ASSERT) {
      return
    }

    try {
      val fileNameTimeStamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
      val logTimeStamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
      val fileName = "$fileNameTimeStamp.html"

      // Create file
      generateFile(fileName)?.let { file ->
        FileWriter(file, true)
          .also {
            it.append("<p> <strong>$logTimeStamp</strong>   ")
            it.append("<strong>[$tag]</strong><br/>$message</p>")
            it.flush()
            it.close()
          }
      }
    }
    catch (e: Exception) {
      Log.e(LOG_TAG, "Error while logging into file : $e")
    }
  }

  override fun createStackElementTag(element: StackTraceElement): String? {
    // Add log statements line number to the log
    return super.createStackElementTag(element) + " - " + element.lineNumber
  }

  companion object {
    private val LOG_TAG = FileLoggingTree::class.java.simpleName

    /*  Helper method to create file */
    private fun generateFile(fileName: String): File? {
      var file: File? = null
      if (isExternalStorageAvailable) {
        val root = DeviceUtils.logDirectory()
        var dirExists = true
        if (!root.exists()) {
          dirExists = root.mkdirs()
        }
        if (dirExists) {
          file = File(root, fileName)
        }
      }
      return file
    }

    /* Helper method to determine if external storage is available*/
    private val isExternalStorageAvailable: Boolean
      get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
  }
}