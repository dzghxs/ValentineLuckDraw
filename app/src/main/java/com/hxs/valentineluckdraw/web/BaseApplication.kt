package com.hxs.valentineluckdraw.web

import android.app.Application
import android.content.Context
import android.os.Environment
import com.hjq.toast.ToastUtils
import me.hgj.jetpackmvvm.base.BaseApp


class BaseApplication : BaseApp() {

    companion object {
        var _context: Application? = null
        val Neves =
            Environment.getExternalStorageDirectory().toString() + "/neves/"
        fun getContext(): Context {
            return _context!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        _context = this
        ToastUtils.init(this)
    }

}