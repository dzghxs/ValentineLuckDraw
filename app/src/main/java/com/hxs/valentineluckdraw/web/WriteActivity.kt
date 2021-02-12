package com.hxs.valentineluckdraw.web

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.cc.neves.ui.page.write.WriteViewModel
import com.cc.neves.utils.PreferenceUtils
import com.github.lzyzsd.jsbridge.BridgeWebViewClient
import com.github.lzyzsd.jsbridge.DefaultHandler
import com.hjq.toast.ToastUtils
import com.hxs.valentineluckdraw.R
import com.hxs.valentineluckdraw.databinding.ActivityWriteBinding
import top.zibin.luban.Luban
import java.io.File

class WriteActivity : BaseActivity<WriteViewModel, ActivityWriteBinding>(),
        ZpWebChromeClient.OpenFileChooserCallBack {

    override fun layoutId() = R.layout.activity_write

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.CheckPermissionInit(this)
        WebInit()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun WebInit() {
        mDatabind.web.setBackgroundColor(Color.TRANSPARENT)
        mDatabind.web.setDefaultHandler(DefaultHandler())
        val zpWebChromeClient = ZpWebChromeClient()
        mDatabind.web.webChromeClient = zpWebChromeClient
        mDatabind.web.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            mViewModel.downBlobUrl(mDatabind.web, url)
        }
        mDatabind.web.webViewClient = object : BridgeWebViewClient(mDatabind.web) {

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (url.contains("http://school.core.qiqiaocode.com/scratch")) {
                    val key = "pro__Access-Token"
                    val js = "window.localStorage.setItem('$key','${
                        mViewModel.local(let {
                            PreferenceUtils.getString(
                                    this@WriteActivity,
                                    "token"
                            )
                        } as String)
                    }');"
                    view.evaluateJavascript(js) { value: String ->
                        println(
                                "localStorage:$value"
                        )
                    }
                }
            }
        }
        mDatabind.web.addJavascriptInterface(object : Any() {
            @JavascriptInterface
            fun down(base64: String?) {
                mViewModel.Base64Data.postValue(base64)
            }
        }, "java")
        val webSettings = mDatabind.web.settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webSettings.loadsImagesAutomatically = true
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.domStorageEnabled = true
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.databaseEnabled = true
        mDatabind.web.setInitialScale(39)
        CookieManager.getInstance().setAcceptThirdPartyCookies(mDatabind.web, true)
        zpWebChromeClient.setOpenFileChooserCallBack(this)
        if (intent.getStringExtra("url") != null && !intent.getStringExtra("url").equals("")) {
            val map = HashMap<String, String>()
            map["XX-Token"] = let { PreferenceUtils.getString(it, "token") } as String
            map["XX-Device-Type"] = "pc"
            map["XX-Api-Version"] = "1.0"
            mDatabind.web.loadUrl(intent.getStringExtra("url") as String, map)
        } else {
            webSettings.userAgentString =
                    "Mozilla/5.0 (X11; CrOS x86_64 11647.154.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrom    e/73.0.3683.114 Safari/537.36"
            mDatabind.web.loadUrl("file:///android_asset/html/index.html")
        }

    }

    override fun openFileChooserCallBack(uploadMsg: ValueCallback<Uri>, acceptType: String) {
        mViewModel.mUploadMsg = uploadMsg
        showSelect(null)
    }

    override fun showFileChooserCallBack(
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: WebChromeClient.FileChooserParams
    ) {
        mViewModel.filePathCallback = filePathCallback
        showSelect(fileChooserParams)
    }

    override fun showPermissionRequest(request: PermissionRequest?) {
        requestCameraPermissions(request as PermissionRequest)
    }

    /**
     * 打开选择
     */
    private fun showSelect(fileChooserParams: WebChromeClient.FileChooserParams?) {
        val intent = fileChooserParams!!.createIntent()
        when {
            fileChooserParams!!.acceptTypes[0] == ".sb" -> {
                mViewModel.Select_type = 0
                intent.type = "*/*"
                startActivityForResult(intent, mViewModel.REQUEST_SELECT_FILE_CODE)
            }
            fileChooserParams.acceptTypes[0] == ".svg" -> {
                mViewModel.Select_type = 1
                intent.type = "image/*"
                startActivityForResult(intent, mViewModel.REQUEST_SELECT_FILE_CODE)
            }
            fileChooserParams.acceptTypes[0] == ".wav" -> {
                mViewModel.Select_type = 2
                intent.type = "audio/*"
                startActivityForResult(intent, mViewModel.REQUEST_SELECT_FILE_CODE)
            }
            else -> {
                try {
                    mViewModel.Select_type = 3
                    startActivityForResult(intent, mViewModel.REQUEST_SELECT_FILE_CODE)
                } catch (e: ActivityNotFoundException) {
                    mViewModel.mUploadMsgs = null
                }
            }
        }
        mViewModel.mUploadMsgs = null
    }

    fun takeCameraPhoto() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            ToastUtils.show("设备无摄像头")
            return
        }
        val filePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
        mViewModel.mFileFromCamera = File(filePath, System.nanoTime().toString() + ".jpg")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imgUrl: Uri = if (applicationInfo.targetSdkVersion > Build.VERSION_CODES.M) {
            FileProvider.getUriForFile(
                    this,
                    mViewModel.authority,
                    mViewModel.mFileFromCamera!!
            )
        } else {
            Uri.fromFile(mViewModel.mFileFromCamera)
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUrl)
        startActivityForResult(intent, mViewModel.REQUEST_FILE_CAMERA_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            resultCode == Activity.RESULT_OK -> {
                if (mViewModel.filePathCallback == null) {
                    mViewModel.Select_type = -1
                    mViewModel.filePathCallback!!.onReceiveValue(null)
                    mViewModel.filePathCallback = null
                    return
                }
                val result =
                        if (data == null || resultCode != AppCompatActivity.RESULT_OK) null else data.data
                try {
                    when {
                        mViewModel.Select_type == 0 -> {
                            val uris: Array<out Uri>? =
                                    WebChromeClient.FileChooserParams.parseResult(resultCode, data)
                            mViewModel.filePathCallback!!.onReceiveValue(uris as Array<Uri>)
                        }
                        mViewModel.Select_type == 1 -> {
                            val uris: Array<out Uri>? =
                                    WebChromeClient.FileChooserParams.parseResult(resultCode, data)
                            val uriimgs: Array<Uri?> = arrayOfNulls(uris!!.size)
                            for (i in uris.indices) {
                                uriimgs[i] = Uri.fromFile(
                                        Luban.with(this)
                                                .load(PathUtils.getPath(this, uris[i])).get()[i]
                                )
                            }
                            mViewModel.filePathCallback!!.onReceiveValue(uriimgs as Array<Uri>)
                        }
                        mViewModel.Select_type == 2 -> {
                            val uris: Array<out Uri>? =
                                    WebChromeClient.FileChooserParams.parseResult(resultCode, data)
                            val uriimgs: Array<Uri?> = arrayOfNulls(uris!!.size)
                            for (i in uris.indices) {
                                uriimgs[i] = Uri.fromFile(
                                        Luban.with(this)
                                                .load(PathUtils.getPath(this, uris[i])).get()[i]
                                )
                            }
                            mViewModel.filePathCallback!!.onReceiveValue(uriimgs as Array<Uri>)
                        }
                        mViewModel.Select_type == 3 -> {
                            val uris: Array<out Uri>? =
                                    WebChromeClient.FileChooserParams.parseResult(resultCode, data)
                            val uriimgs: Array<Uri?> = arrayOfNulls(uris!!.size)
                            for (i in uris.indices) {
                                uriimgs[i] = Uri.fromFile(
                                        Luban.with(this)
                                                .load(PathUtils.getPath(this, uris[i])).get()[i]
                                )
                            }
                            mViewModel.filePathCallback!!.onReceiveValue(uriimgs as Array<Uri>)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mViewModel.filePathCallback!!.onReceiveValue(null)
                    mViewModel.filePathCallback = null
                }
                mViewModel.Select_type = -1
            }
            resultCode == Activity.RESULT_CANCELED -> {
                if (mViewModel.filePathCallback != null) {
                    //xie ：直接点击取消时，ValueCallback回调会被挂起，需要手动结束掉回调，否则再次点击选择照片无响应
                    mViewModel.filePathCallback!!.onReceiveValue(null)
                    mViewModel.filePathCallback = null
                }
            }
        }
    }

    /* access modifiers changed from: private */
    fun requestCameraPermissions(permissionRequest2: PermissionRequest) {
        mViewModel.permissionRequest = permissionRequest2
        val arrayList = ArrayList<Any>()
        for (str in permissionRequest2.resources) {
            if (str == "android.webkit.resource.VIDEO_CAPTURE" && ContextCompat.checkSelfPermission(
                            this,
                            mViewModel.ANDROID_PERMISSION_CAMERA
                    ) != 0
            ) {
                arrayList.add(mViewModel.ANDROID_PERMISSION_CAMERA)
            }
            if (str == "android.webkit.resource.AUDIO_CAPTURE" && ContextCompat.checkSelfPermission(
                            this,
                            mViewModel.ANDROID_PERMISSION_RECORD_AUDIO
                    ) != 0
            ) {
                arrayList.add(mViewModel.ANDROID_PERMISSION_RECORD_AUDIO)
            }
        }
        if (!arrayList.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    (arrayList.toArray(arrayOfNulls<String>(arrayList.size)) as Array<String?>),
                    2
            )
        } else {
            grantPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun grantPermission() {
        val permissionRequest2: PermissionRequest =
                mViewModel.permissionRequest as PermissionRequest
        permissionRequest2.grant(permissionRequest2.resources)
        mViewModel.permissionRequest = null
    }

    override fun dismissLoading() {
    }

    override fun showLoading(message: String) {
    }

}