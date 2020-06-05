package com.summer.demo.module.video.util

/**
 * Created by dell on 2017/6/22.
 */

import android.net.Uri

import com.coremedia.iso.boxes.Container
import com.googlecode.mp4parser.FileDataSourceViaHeapImpl
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.util.LinkedList

/**
 * 裁剪视频工具类
 */
object TrimVideoUtils {

    @Throws(IOException::class)
    fun startTrim(src: File, outFilePath: String, startMs: Long, endMs: Long, callback: OnTrimVideoListener) {
        val file = File(outFilePath)
        genVideoUsingMp4Parser(src, file, startMs, endMs, callback)
    }

    @Throws(IOException::class)
    private fun genVideoUsingMp4Parser(src: File, dst: File, startMs: Long, endMs: Long, callback: OnTrimVideoListener) {
        // NOTE: Switched to using FileDataSourceViaHeapImpl since it does not use memory mapping (VM).
        // Otherwise we get OOM with large movie files.
        val movie = MovieCreator.build(FileDataSourceViaHeapImpl(src.absolutePath))

        val tracks = movie.tracks
        movie.tracks = LinkedList()
        // remove all tracks we will create new tracks from the old

        for (track in tracks) {
            var currentSample: Long = 0
            var currentTime = 0.0
            var startSample1 = -1L
            var endSample1 = -1L

            for (i in 0 until track.sampleDurations.size) {
                val delta = track.sampleDurations[i] * 1.0
                if (currentTime <= startMs) {
                    // current sample is still before the new starttime
                    startSample1 = currentSample
                } else if (currentTime <= endMs) {
                    //                     current sample is after the new start time and still before the new endtime
                    endSample1 = currentSample
                } else {
                    break
                }
                currentTime += 1000.0 * delta / track.trackMetaData.timescale
                currentSample++

            }
            movie.addTrack(AppendTrack(CroppedTrack(track, startSample1, endSample1)))
        }

        dst.parentFile.mkdirs()

        if (!dst.exists()) {
            dst.createNewFile()
        }

        val out = DefaultMp4Builder().build(movie)

        val fos = FileOutputStream(dst)
        val fc = fos.channel
        out.writeContainer(fc)

        fc.close()
        fos.close()
        callback?.getResult(Uri.parse(dst.toString()))
    }

}