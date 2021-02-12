package com.cc.neves.utils

import android.util.Base64
import com.hxs.valentineluckdraw.web.BaseApplication
import java.io.File
import java.io.FileOutputStream

object Base64Utils {

    fun decoderBase64File(base64Code: String?, savePath: String?) {
        val buffer = Base64.decode(base64Code, Base64.URL_SAFE)
        val out = FileOutputStream(savePath)
        out.write(buffer)
        out.close()
    }

    fun convertToGifAndProcess(base64: String?, name: String) {
        var s = ""
        if (name == "")
            s = ("七巧编程" + System.currentTimeMillis())
        else
            s = name
        val gifFile =
            File(BaseApplication.Neves + s + "_.sb3")
        saveSb3ToPath(base64, gifFile)
    }

    private fun saveSb3ToPath(base64: String?, gifFilePath: File) {
        try {
            val fileBytes = Base64.decode(
                base64!!.replaceFirst(
                    "data:application/x.scratch.sb3;base64,".toRegex(), ""
                ), 0
            )
            val os = FileOutputStream(gifFilePath, false)
            os.write(fileBytes)
            os.flush()
            os.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}