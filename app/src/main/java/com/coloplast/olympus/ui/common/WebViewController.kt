package com.coloplast.olympus.ui.common

import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bluelinelabs.conductor.RouterTransaction
import com.coloplast.olympus.R
import com.coloplast.olympus.databinding.ControllerWebviewBinding


/**
 * Created by Kike Bodi (ebp@dawnhealth.com) on 16/07/2020.
 * Copyright by Dawn Health A/S. All rights reserved.
 */
class WebViewController(args: Bundle) : BaseController<ControllerWebviewBinding>(args) {

  private val targetUrl = args.getString(URL)!!

  override fun attachVariables(viewBinding: ControllerWebviewBinding) {
    viewBinding.controller = this
  }

  override fun onViewBound(viewBinding: ControllerWebviewBinding) {
    // Load URL
    viewBinding.webView.apply {

      settings.javaScriptEnabled = false //enable only if necessary
      loadUrl(targetUrl)

      webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
          view.loadUrl(request.url.toString())
          return false
        }
      }
    }
  }

  override fun getLayout(): Int = R.layout.controller_webview

  companion object {
    private const val TAG_CONTROLLER = "web_view_controller"
    private const val URL = "URL"

    fun getRouterTransaction(url: String): RouterTransaction {
      val args = Bundle().apply {
        putString(URL, url)
      }
      return RouterTransaction.with(WebViewController(args))
        .tag(TAG_CONTROLLER)
    }
  }
}