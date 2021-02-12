package com.cc.neves.ui.page.write

import android.app.Activity
import android.net.Uri
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import com.hjq.permissions.OnPermission
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.hxs.valentineluckdraw.web.BaseApplication
import com.hxs.valentineluckdraw.web.FinishWebView
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData
import java.io.File

class WriteViewModel : BaseViewModel() {
    val ANDROID_PERMISSION_CAMERA = "android.permission.CAMERA"
    val ANDROID_PERMISSION_RECORD_AUDIO = "android.permission.RECORD_AUDIO"

    var Select_type = -1

    val REQUEST_SELECT_FILE_CODE = 100
    val REQUEST_FILE_CHOOSER_CODE = 101
    val REQUEST_FILE_CAMERA_CODE = 102

    // 默认图片压缩大小（单位：K）
    val IMAGE_COMPRESS_SIZE_DEFAULT = 400

    // 压缩图片最小高度
    val COMPRESS_MIN_HEIGHT = 900

    // 压缩图片最小宽度
    val COMPRESS_MIN_WIDTH = 675

    var mUploadMsg: ValueCallback<Uri>? = null
    var mUploadMsgs: ValueCallback<Array<Uri>>? = null

    // 相机拍照返回的图片文件
    var mFileFromCamera: File? = null
    val mTakePhotoFile: File? = null

    var filePathCallback: ValueCallback<Array<Uri>>? = null

    val authority = "com.cc.neves.provider"

    var permissionRequest: PermissionRequest? = null

    var Base64Data = StringLiveData()

    /**
     * 下载、Blob图片
     *
     * @param url
     */
    fun downBlobUrl(web: FinishWebView, url: String) {
        if (url.startsWith("blob")) {
            val blob = "  var request = new XMLHttpRequest();" +
                    "        request.open('GET', '" + url + "', true);" +
                    "        request.setRequestHeader('Content-type', 'text/plain');" +
                    "        request.responseType = 'blob';" +
                    "        request.onload = function (e) {" +
                    "            if (this.status === 200) {" +
                    "                var blobFile = this.response;" +
                    "                var reader = new FileReader();" +
                    "                reader.readAsDataURL(blobFile);" +
                    "                reader.onloadend = function() {" +
                    "                var base64data = reader.result;" +
                    "                window.java.down(base64data);" +
                    "                }" +
                    "             }" +
                    "        };" +
                    "        request.send();"
            val js = "javascript:$blob"
            web.evaluateJavascript(js,null)
        }
    }

    /**
     * 验证权限
     */
    fun CheckPermissionInit(context: Activity) {
        XXPermissions.with(context)
//                .permission(*Permission.Group.STORAGE)
            .permission(Permission.CAMERA)
            .permission(Permission.RECORD_AUDIO)
            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
            .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
            .request(object : OnPermission {
                override fun hasPermission(
                    granted: List<String>,
                    isAll: Boolean
                ) {
                    val file = File(BaseApplication.Neves)
                    //判断文件夹是否存在,如果不存在则创建文件夹
                    if (!file.exists()) {
                        file.mkdir()
                    }
                }

                override fun noPermission(
                    denied: List<String>,
                    quick: Boolean
                ) {
                    ToastUtils.show("必要权限未提供，app即将关闭")
                    try {
                        Thread.sleep(2000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    context.finish()
                }
            })
    }

    fun local(token: String): String {
        val access =
            "{\"value\":\"" + token + "\",\"expire\":\"" + (System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000) + "\"}"
        return access
    }
}