package com.hxs.valentineluckdraw.web

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.webkit.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class ZpWebChromeClient : WebChromeClient() {
    private var mOpenFileChooserCallBack: OpenFileChooserCallBack? = null

    override fun onPermissionRequest(request: PermissionRequest?) {
        mOpenFileChooserCallBack!!.showPermissionRequest(request)
    }

    //For Android 3.0 - 4.0
    // For Android < 3.0
    @JvmOverloads
    fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String = "") {
        if (mOpenFileChooserCallBack != null) {
            mOpenFileChooserCallBack!!.openFileChooserCallBack(uploadMsg, acceptType)
        }
    }

    // For Android 4.0 - 5.0
    fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
        openFileChooser(uploadMsg, acceptType)
    }

    override fun onConsoleMessage(cm: ConsoleMessage?): Boolean {
        println(("JavaScript log, " + cm!!.sourceId() + ":" + cm.lineNumber() + ", " + cm.message()))
        return true
    }

    // For Android > 5.0
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {
        if (mOpenFileChooserCallBack != null) {
            mOpenFileChooserCallBack!!.showFileChooserCallBack(filePathCallback, fileChooserParams)
        }
        return true
    }

    fun setOpenFileChooserCallBack(callBack: OpenFileChooserCallBack?) {
        mOpenFileChooserCallBack = callBack
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
    }

    interface OpenFileChooserCallBack {
        fun openFileChooserCallBack(uploadMsg: ValueCallback<Uri>, acceptType: String)
        fun showFileChooserCallBack(filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams)
        fun showPermissionRequest(request: PermissionRequest?)
    }

}