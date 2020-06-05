package com.summer.demo.module.video.util

import android.app.Activity

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException


/**
 * Created by karan on 13/2/15.
 */
class Compressor(var a: Activity) {
    var ffmpeg: FFmpeg

    init {
        ffmpeg = FFmpeg.getInstance(a)
    }

    fun loadBinary(mListener: InitListener) {
        try {
            ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {
                override fun onStart() {}

                override fun onFailure() {
                    mListener.onLoadFail("incompatible with this device")
                }

                override fun onSuccess() {
                    mListener.onLoadSuccess()
                }

                override fun onFinish() {

                }
            })
        } catch (e: FFmpegNotSupportedException) {
            e.printStackTrace()
        }

    }

    fun execCommand(cmd: String, mListener: CompressListener) {
        try {
            val cmds = cmd.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            ffmpeg.execute(cmds, object : ExecuteBinaryResponseHandler() {

                override fun onStart() {}

                override fun onProgress(message: String) {
                    mListener.onExecProgress(message)
                }

                override fun onFailure(message: String) {
                    mListener.onExecFail(message)
                }

                override fun onSuccess(message: String) {
                    mListener.onExecSuccess(message)
                }

                override fun onFinish() {}
            })
        } catch (e: FFmpegCommandAlreadyRunningException) {
            e.printStackTrace()
        }

    }


}
