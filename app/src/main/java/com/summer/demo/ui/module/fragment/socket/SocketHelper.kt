package com.summer.demo.ui.module.fragment.socket

import android.os.Handler
import android.os.Message
import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.summer.demo.bean.BaseResp
import com.summer.helper.server.PostData
import com.summer.helper.utils.Logs
import com.summer.helper.utils.SFileUtils
import com.summer.helper.utils.STextUtils
import com.summer.helper.utils.SThread
import java.io.*
import java.lang.ref.WeakReference
import java.net.InetSocketAddress
import java.net.Socket

/**
 * socket连接
 */
class SocketHelper {

    private var client: Socket? = null
    private var inputStream: InputStream? = null
    private var br: BufferedReader? = null

    private var connectIp: String? = null
    internal var connectPort: Int = 0

    internal lateinit var ledInfo: BaseResp//如果服务器登录需要相关信息验证的话，在连接前，将该信息传过来

    internal var socketResponseListener: SocketResponseListener? = null

    internal val MSG_HEATBEAT = 0//心跳
    internal val MSG_RECONNECT = 1//重连
    internal val MSG_CHECK_CONNECT = 2//检测连接状态
    internal val MSG_LOGIN = 3//登录

    internal var cmdNo = 450//指令序号起始值
    internal var myHandler: MyHandler

    internal var waitFinish = false

    internal var isLogining = false


    /**
     * 上传文件
     * 开启新端口
     *
     * @param path
     */
    private var uploadFileSocket: Socket? = null

    /**
     * 开始上传
     *
     * @param path
     */
    internal var dos: DataOutputStream? = null

    init {
        myHandler = MyHandler(this)
    }

    /**
     * 开始连接,关闭，连接，与收消息必须在一个线程里，否则难以维护，可能引发问题
     *
     * @param ip
     * @param port
     */
    fun connect(ip: String?, port: Int) {
        waitFinish = false
        this.connectIp = ip
        this.connectPort = port
        SThread.getIntances().clear()
        SThread.getIntances().submit {
            if (client != null) {
                close(10)
                client = null
            }
            try {
                Thread.sleep(300)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            client = Socket()
            try {
                client!!.connect(InetSocketAddress(ip, port), 10000)
                Thread.sleep(200)
                if (client!!.isConnected) {
                    //当前连接成功后，如果该服务器需要登录，则立即发送登录指令
                    myHandler.sendEmptyMessage(MSG_LOGIN)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Logs.i("e::$e")
            } catch (e: InterruptedException) {
                e.printStackTrace()
                Logs.i("e::$e")
            }

            startHeartBean()
            receiveData()
            while (client!!.isConnected) {
                receiveData()
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }


    }

    fun reconnect() {
        myHandler.removeMessages(MSG_CHECK_CONNECT)
        cancelHeatBeat()
        Logs.i("正在重连")
        connect(connectIp, connectPort)
    }


    fun receiveData() {
        var isr: InputStreamReader? = null
        try {
            inputStream = client!!.getInputStream()
            isr = InputStreamReader(inputStream!!)
            br = BufferedReader(isr)
            var info: String? = null
            while(true){
                info = br!!.readLine()
                if(info == null){
                    break
                }
                waitFinish = false
                Logs.i("i::" + info!!)
                myHandler.removeMessages(MSG_CHECK_CONNECT)
                handleResponse(info)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {

        }
    }

    /**
     * 处理回复
     */
    private fun handleResponse(content: String) {
        val baseResp = JSON.parseObject(content, BaseResp::class.java)
        val msg = baseResp.msg
        val jsonObject = JSONObject.parseObject(msg)
        if (jsonObject != null) {
            val error = jsonObject.getString("ErrorMessage")
            if (!TextUtils.isEmpty(error)) {
                //收到错误消息，及相关处理
                return
            }
        }
        val `fun` = baseResp.getFun()
        when (`fun`) {
            SocketFun.FUN_LOGIN -> isLogining = true
            SocketFun.FUN_TEST ->
                //根据服务器返回的值，做相关的操作
                if (socketResponseListener != null) {
                    socketResponseListener!!.response(SocketFun.FUN_TEST, 0)
                }
        }
    }

    fun sendData(data: String, isLogin: Boolean) {
        if (!isLogin && !isLogining) {
            //return;
        }
        myHandler.sendEmptyMessageDelayed(MSG_CHECK_CONNECT, 2000)
        if (waitFinish) {
            return
        }
        cancelHeatBeat()
        startHeartBean()
        waitFinish = true
        if (client!!.isClosed) {
            Logs.i("设备已关闭")
            myHandler.sendEmptyMessageDelayed(MSG_RECONNECT, 1000)
            myHandler.removeMessages(MSG_CHECK_CONNECT)
            return
        }
        SThread.getIntances().submit {
            var os: OutputStream? = null
            val pw: PrintWriter? = null

            try {
                os = client!!.getOutputStream()
                os!!.write(data.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 构建心跳包
     *
     * @return
     */
    private fun buildHeatbeat(): String {
        val jsonObject = JSONObject()
        jsonObject["Fun"] = "02"
        jsonObject["CmdNo"] = getCmdNo()
        val dataObject = JSONObject()
        dataObject["SessionID"] = PostData.TOKEN
        jsonObject["Data"] = dataObject.toJSONString()
        return jsonObject.toJSONString()
    }

    /**
     * 发送心跳包
     */
    fun sendHeatBeat() {
        Logs.i("当前设备是否在连接中：" + client!!.isConnected)
        val data = buildHeatbeat()
        sendData(data, false)
    }

    fun setLedInfo(ledInfo: BaseResp) {
        this.ledInfo = ledInfo
    }

    /**
     * 发送登录
     */
    fun sendLogin() {
        if (client == null) {
            return
        }
        val data = buildLogin()
        Logs.i("当前设备是否在连接中：" + client!!.isConnected)
        sendData(data, true)
    }

    /**
     * 构建登录包
     *
     * @return
     */
    private fun buildLogin(): String {
        val jsonObject = JSONObject()
        jsonObject["Fun"] = "01"
        jsonObject["CmdNo"] = getCmdNo()
        val dataObject = JSONObject()
        dataObject["Type"] = 6
        //其它需要验证的
        jsonObject["Data"] = dataObject.toJSONString()
        return jsonObject.toJSONString()
    }

    /**
     * 开启另外一个端口写文件，如果服务器是这么定的话
     *
     * @param path
     */
    fun uploadFile(path: String) {
        closeUpload()
        uploadFileSocket = Socket()
        SThread.getIntances().submit {
            try {

                uploadFileSocket!!.connect(InetSocketAddress(connectIp, 6698), 2000)
                SThread.getIntances().submit {
                    try {
                        Thread.sleep(300)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    startUploadFile(path)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Logs.i("e::$e")
            }
        }
    }

    private fun startUploadFile(path: String) {
        SThread.getIntances().submit(Runnable {
            var dis: DataInputStream? = null

            val file = File(path)
            try {
                dis = DataInputStream(FileInputStream(file))
                dos = DataOutputStream(uploadFileSocket!!.getOutputStream())
                var count = dis.available().toLong()
                while (count == 0L) {
                    count = dis.available().toLong()
                }
                //注意大小端的问题
                dos!!.write(STextUtils.getBytes(count))
                dos!!.flush()
                Thread.sleep(200)
                dos!!.write(SFileUtils.getFileName(path)!!.toByteArray(charset("UTF-8")))
                dos!!.flush()
                Thread.sleep(200)
                val packetSize = 1024 * 1000
                val buffer = ByteArray(packetSize)
                var len = 0
                Logs.i("上传文件")
                while(true){
                    len = dis.read(buffer)
                    if(len == -1){
                        break
                    }
                    Logs.i("上传文件")
                    dos!!.write(buffer, 0, len)
                    Thread.sleep(200)
                }
                dos!!.flush()

                //开始读
                var isr: InputStreamReader? = null
                var br: BufferedReader? = null
                val inputStream: InputStream
                if (uploadFileSocket!!.isClosed) {
                    return@Runnable
                }
                inputStream = uploadFileSocket!!.getInputStream()
                isr = InputStreamReader(inputStream)
                br = BufferedReader(isr)
                var info: String? = null
                while (true){
                    info = br.readLine()
                    if(info == null){
                        break
                    }
                    Logs.i("info:" + info!!)
                    handleUploadResponse(info)
                    try {
                        closeUpload()
                        isr?.close()
                        br?.close()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                        Logs.i("e::$e1")
                    }
                }
            } catch (e: FileNotFoundException) {
                Logs.i("e:$e")
                closeUpload()
                e.printStackTrace()
            } catch (e: IOException) {
                Logs.i("e:$e")
                closeUpload()
                e.printStackTrace()
            } catch (e: InterruptedException) {
                Logs.i("e:$e")
                e.printStackTrace()
            } finally {
                if (dis != null) {
                    try {
                        dis.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        })

    }


    private fun closeUpload() {
        Logs.i("close:")
        try {
            if (dos != null) {

                dos!!.close()
            }
            if (uploadFileSocket != null) {
                uploadFileSocket!!.close()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    /**
     * 上传文件后收到的回复
     *
     * @param content
     */
    private fun handleUploadResponse(content: String?) {

    }

    /**
     * 开始发送心跳
     */
    fun startHeartBean() {
        myHandler.sendEmptyMessageDelayed(MSG_HEATBEAT, 10000)
    }

    fun cancelHeatBeat() {
        myHandler.removeMessages(MSG_HEATBEAT)
    }

    /**
     * 处理MyHandler派发的消息
     *
     * @param position
     * @param object
     */
    fun handleMsg(position: Int, `object`: Any) {
        when (position) {
            MSG_HEATBEAT -> {
                sendHeatBeat()
                startHeartBean()
            }
            MSG_CHECK_CONNECT -> if (waitFinish) {
                reconnect()
            }
            MSG_LOGIN -> sendLogin()
        }
    }

    class MyHandler(activity: SocketHelper) : Handler() {
        private val mActivity: WeakReference<SocketHelper>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            activity?.handleMsg(msg.what, msg.obj)
        }
    }

    fun close(where: Int) {
        Logs.i("where<<$where")
        try {
            if (inputStream != null) {
                //inputStream.close();
            }

            if (br != null) {
                br!!.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        waitFinish = false
        myHandler.removeMessages(MSG_CHECK_CONNECT)
        cancelHeatBeat()
        if (client != null) {
            Logs.i("closed")
            try {
                client!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    /**
     * 范围450-500，自动循环，每次发送命令序号递增
     *
     * @return
     */
    private fun getCmdNo(): Int {
        cmdNo++
        if (cmdNo > 500) {
            cmdNo = 450
        }
        return cmdNo
    }

}
