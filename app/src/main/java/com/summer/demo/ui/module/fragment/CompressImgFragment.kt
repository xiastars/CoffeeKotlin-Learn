package com.summer.demo.ui.module.fragment

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.ghnor.flora.Flora
import com.ghnor.flora.spec.decoration.Decoration
import com.summer.demo.R
import com.summer.demo.module.album.listener.SizeCalculation
import com.summer.demo.module.album.util.SelectPhotoHelper
import com.summer.demo.module.base.BaseFragment
import com.summer.helper.listener.OnResponseListener
import com.summer.helper.server.PostData
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SFileUtils
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/10/21 17:30
 */
class CompressImgFragment : BaseFragment() {
    internal lateinit var selectPhotoHelper: SelectPhotoHelper

    override fun initView(view: View) {

        selectPhotoHelper = SelectPhotoHelper(context!!, object : OnResponseListener {
            override fun succeed(url: String) {

            }

            override fun failure() {

            }
        })
        selectPhotoHelper.enterToAlbum()
    }


    private fun compressFile(url: String) {
        Flora.with()
                // 配置inSample和quality的算法，内置了一套基于Luban的压缩算法
                .calculation(object : SizeCalculation() {
                    override fun calculateInSampleSize(srcWidth: Int, srcHeight: Int): Int {
                        return super.calculateInSampleSize(srcWidth, srcHeight)
                    }

                    override fun calculateQuality(srcWidth: Int, srcHeight: Int, targetWidth: Int, targetHeight: Int): Int {
                        return super.calculateQuality(srcWidth, srcHeight, targetWidth, targetHeight)
                    }
                })
                // 对压缩后的图片做个性化地处理，如：添加水印
                .addDecoration(object : Decoration() {
                    override fun onDraw(bitmap: Bitmap): Bitmap {
                        return super.onDraw(bitmap)
                    }
                })
                // 配置Bitmap的色彩格式
                .bitmapConfig(Bitmap.Config.RGB_565)
                // 同时可进行的最大压缩任务数量
                .compressTaskNum(1)
                // 安全内存，设置为2，表示此次压缩任务需要的内存小于1/2可用内存才进行压缩任务
                .safeMemory(2)
                // 压缩完成的图片在磁盘的存储目录
                .diskDirectory(File(SFileUtils.getImageViewDirectory()))
                .load(url)
                .compress { s -> Logs.i("fileSize::::" + File(s).length() / 1024 + ",,,file:" + s) }
    }

    public override fun loadData() {

    }

    override fun dealDatas(requestType: Int, obj: Any) {

    }

    override fun setContentView(): Int {
        return R.layout.view_empty
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        /**
         * 上传文件测试
         * @param context
         * @param session
         * @param uploadUrl
         * @param oldFilePath
         */
        fun uploadMedia(context: Context, session: String, uploadUrl: String, oldFilePath: String) {
            try {
                val url = URL(uploadUrl)
                val con = url.openConnection() as HttpURLConnection

                // 允许Input、Output，不使用Cache
                con.doInput = true
                con.doOutput = true
                con.useCaches = false

                con.connectTimeout = 50000
                con.readTimeout = 50000
                // 设置传送的method=POST
                con.requestMethod = "POST"
                //在一次TCP连接中可以持续发送多份数据而不会断开连接
                con.setRequestProperty("Connection", "close")
                //设置编码

                con.setRequestProperty("Charset", "UTF-8")
                //text/plain能上传纯文本文件的编码格式
                //con.setRequestProperty("Content-Type", "text/plain");
                con.setRequestProperty("Content-Type", "application/octet-stream")
                con.addRequestProperty("sessionID", session)
                con.addRequestProperty("ext", if (oldFilePath.endsWith(SFileUtils.FileType.FILE_MP4)) "mp4" else "png")
                con.addRequestProperty("file", "")
                // 设置DataOutputStream
                val ds = DataOutputStream(con.outputStream)

                // 取得文件的FileInputStream
                val fStream = FileInputStream(oldFilePath)
                // 设置每次写入1024bytes
                val bufferSize = 1024
                val buffer = ByteArray(bufferSize)

                var length = -1
                // 从文件读取数据至缓冲区
                while(true){
                    length = fStream.read(buffer)
                    if (length == -1) {
                        break
                    }
                    ds.write(buffer, 0, length)
                }
                val strBuf = StringBuffer()
                var reader: BufferedReader? = BufferedReader(InputStreamReader(con.inputStream))
                var line: String? = null
                while(true){
                    line = reader!!.readLine() ?: break
                    strBuf.append(line).append("\n")
                }

                val res = strBuf.toString()
                Logs.i("res:::$res")
                ds.flush()
                fStream.close()
                ds.close()

                reader!!.close()
                reader = null
                Logs.i("con.get:$con")
                if (con.responseCode == 200) {
                    Logs.i("con:" + con.responseMessage + ",," + con.content + ",,")
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }

        }

        /**
         * 上传图片
         * @param urlStr
         * @param textMap
         * @param fileMap
         * @return
         */
        fun formUpload(urlStr: String, textMap: Map<String, String>?, fileMap: Map<String, String>?): String {
            var res = ""
            var conn: HttpURLConnection? = null
            val BOUNDARY = "---------------------------123821742118716" //boundary就是request头和上传文件内容的分隔符
            try {
                val url = URL(urlStr)
                conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 30000
                conn.doOutput = true
                conn.doInput = true
                conn.useCaches = false
                conn.requestMethod = "POST"
                conn.setRequestProperty("Connection", "Keep-Alive")
                conn.setRequestProperty("User-Agent", PostData.getUserAgent())
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$BOUNDARY")

                val out = DataOutputStream(conn.outputStream)
                // text
                if (textMap != null) {
                    val strBuf = StringBuffer()
                    val iter = textMap.entries.iterator()
                    while (iter.hasNext()) {
                        val entry = iter.next()
                        val inputValue = entry.value ?: continue
                        strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n")
                        strBuf.append("Content-Disposition: form-data; name=\"${entry.key}\"\r\n\r\n")
                        strBuf.append(inputValue)
                    }
                    out.write(strBuf.toString().toByteArray())
                }

                // file
                if (fileMap != null) {
                    val iter = fileMap.entries.iterator()
                    while (iter.hasNext()) {
                        val entry = iter.next()
                        val inputValue = entry.value ?: continue
                        val file = File(inputValue)
                        val filename = file.name

                        val strBuf = StringBuffer()
                        strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n")
                        strBuf.append("Content-Disposition: form-data; name=\"${entry.key}\"; filename=\"$filename\"\r\n")
                        strBuf.append("Content-Type:" + "application/octet-stream")

                        out.write(strBuf.toString().toByteArray())

                        val inputStream = DataInputStream(FileInputStream(file))
                        var bytes = 0
                        val bufferOut = ByteArray(1024)
                        while(true){
                            bytes = inputStream.read(bufferOut)
                            if(bytes == -1){
                                break
                            }

                        }
                        inputStream.close()
                    }
                }

                val endData = "\r\n--$BOUNDARY--\r\n".toByteArray()
                out.write(endData)
                out.flush()
                out.close()

                // 读取返回数据
                val strBuf = StringBuffer()
                var reader: BufferedReader? = BufferedReader(InputStreamReader(conn.inputStream))
                var line: String? = null
                while(true){
                    line = reader!!.readLine() ?: break
                    strBuf.append(line).append("\n")

                }
                res = strBuf.toString()
                Logs.i("res::$res")
                reader!!.close()
                reader = null
            } catch (e: Exception) {
                println("发送POST请求出错。$urlStr")
                e.printStackTrace()
            } finally {
                if (conn != null) {
                    conn.disconnect()
                    conn = null
                }
            }
            return res
        }
    }
}
