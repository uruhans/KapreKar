package com.coloplast.olympus.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.coloplast.olympus.shared.AppSettings
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object AssetUtils {
  
  fun getIntentWithCachedFileProvider(context: Context, filename: String): Intent {
    val file = File(context.cacheDir, filename)
    if (!file.exists()) {
      copyFileToCache(context, filename)
    }
    
    return Intent(
      Intent.ACTION_VIEW,
      FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        File(context.cacheDir, filename)
      )
    ).apply {
      flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
    }
  }
  
  private fun copyFileToCache(context: Context, filename: String) {
    // In this sample, we read a PDF from the assets directory.
    val file = File(context.cacheDir, filename)
    if (!file.exists()) {
      file.createNewFile()
      
      val asset = context.assets.open(filename)
      val output = FileOutputStream(file)
      val buffer = ByteArray(1024)
      var size = asset.read(buffer)
      while (size != -1) {
        output.write(buffer, 0, size)
        size = asset.read(buffer)
      }
      asset.close()
      output.close()
    }
  }
  
  fun getFromAssets(filename: String): String {
    Timber.d("getFromAssets: $filename")
    return try {
      val inputStream: InputStream = AppSettings.applicationContext.resources.assets.open(filename)
      inputStream.bufferedReader()
          .use { it.readText() }
    }
    catch (e: Exception) {
      Timber.e("getFromAssets: $e")
      ""
    }
  }
}