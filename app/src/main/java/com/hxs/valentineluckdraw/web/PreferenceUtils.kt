package com.cc.neves.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceUtils {
    private var mSp: SharedPreferences? = null
    private const val SP_NAME = "config"
    fun getPreferences(context: Context): SharedPreferences? {
        if (mSp == null) {
            mSp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        }
        return mSp
    }

    /**
     * 获得boolean数据
     *
     * @param context
     * @param key
     * @param defValue :没有时的默认值
     * @return
     */
    fun getBoolean(context: Context, key: String?, defValue: Boolean): Boolean {
        val sp = getPreferences(context)
        return sp!!.getBoolean(key, defValue)
    }

    /**
     * 获得boolean数据，如果没有返回false
     *
     * @param context
     * @param key
     * @return
     */
    fun getBoolean(context: Context, key: String?): Boolean {
        return getBoolean(context, key, false)
    }

    /**
     * 设置boolean数据
     *
     * @param context
     * @param key
     * @param value
     */
    fun setBoolean(context: Context, key: String?, value: Boolean) {
        val sp = getPreferences(context)
        val editor = sp!!.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    /**
     * 获得String数据
     *
     * @param context
     * @param key
     * @param defValue :没有时的默认值
     * @return
     */
    fun getString(context: Context, key: String?, defValue: String?): String? {
        val sp = getPreferences(context)
        return sp!!.getString(key, defValue)
    }

    /**
     * 获得String数据，如果没有返回null
     *
     * @param context
     * @param key
     * @return
     */
    fun getString(context: Context, key: String?): String? {
        return getString(context, key, "")
    }

    /**
     * 获得int数据，如果没有返回null
     *
     * @param context
     * @param key
     * @return
     */
    fun getInt(context: Context, key: String?): Int {
        val sp = getPreferences(context)
        return sp!!.getInt(key, 0)
    }

    /**
     * 设置String数据
     *
     * @param context
     * @param key
     * @param value
     */
    fun setString(context: Context, key: String?, value: String?) {
        val sp = getPreferences(context)
        val editor = sp!!.edit()
        editor.putString(key, value)
        editor.commit()
    }

    /**
     * 设置String数据
     *
     * @param context
     * @param key
     * @param value
     */
    fun setInt(context: Context, key: String?, value: Int) {
        val sp = getPreferences(context)
        val editor = sp!!.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    /**
     * 删除用户数据
     *
     * @param context
     */
    fun deleteUserInfo(context: Context) {
        val sp = getPreferences(context)
        val editor = sp!!.edit()
        editor.putString("USERINFO_", "")
        editor.commit()
    }

    /**
     * 获得long数据
     *
     * @param context
     * @param key
     * @param defValue :没有时的默认值
     * @return
     */
    fun getLong(context: Context, key: String?, defValue: Long): Long {
        val sp = getPreferences(context)
        return sp!!.getLong(key, defValue)
    }

    /**
     * 获得long数据，如果没有返回0
     *
     * @param context
     * @param key
     * @return
     */
    fun getLong(context: Context, key: String?): Long {
        return getLong(context, key, 0)
    }

    /**
     * 设置long数据
     *
     * @param context
     * @param key
     * @param value
     */
    fun setLong(context: Context, key: String?, value: Long) {
        val sp = getPreferences(context)
        val editor = sp!!.edit()
        editor.putLong(key, value)
        editor.commit()
    }
}