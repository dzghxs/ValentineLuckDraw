package com.hxs.valentineluckdraw.web

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import me.hgj.jetpackmvvm.base.activity.BaseVmDbActivity
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.getAppViewModel

abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmDbActivity<VM, DB>() {


    /**
     * 当前Activity绑定的视图布局Id abstract修饰供子类实现
     */
    abstract override fun layoutId(): Int

    /**
     * 当前Activityc创建后调用的方法 abstract修饰供子类实现
     */
    abstract override fun initView(savedInstanceState: Bundle?)

    /**
     * 创建liveData数据观察
     */
    override fun createObserver() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * 设置为横屏
         */
        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

}