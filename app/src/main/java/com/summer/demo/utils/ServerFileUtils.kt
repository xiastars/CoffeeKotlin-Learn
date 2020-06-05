package com.summer.demo.utils

import android.content.Context
import java.io.*

/**
 * Created by xiastars on 2017/8/2.
 */

object ServerFileUtils {


    fun readFileByLineOnAsset(code: String?, filePath: String, context: Context): String? {
        if (code == null) {
            return null
        }
        var br: BufferedReader? = null
        var reader: InputStream? = null
        try {
            reader = context.assets.open(filePath)
            val isr = InputStreamReader(reader!!, "UTF-8")
            br = BufferedReader(isr)
            var str: String? = null
            while(true){
                str = br.readLine() ?:break
                if (str != null && str.contains(",")) {
                    val index = str.indexOf(",")
                    val first = str.subSequence(0, index) as String
                    if (code == first) {
                        return str.subSequence(index + 1, str.length) as String
                    }
                }
            }

            br.close()
            reader.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (br != null && reader != null) {
                    br.close()
                    reader.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return null
    }
}
