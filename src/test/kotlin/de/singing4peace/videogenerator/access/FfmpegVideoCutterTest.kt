package de.singing4peace.videogenerator.access

import org.junit.jupiter.api.Test
import java.io.File

internal class FfmpegVideoCutterTest {


    @Test
    fun concatenate() {
        val list = listOf("/home/daniel/Downloads/queen1.mp4", "/home/daniel/Downloads/queen2.mp4")
        val cutter = FfmpegVideoCutter()
        val output = cutter.concatenate(list)
        println(output.absolutePath)
    }

    @Test
    fun replaceAudio() {
        val downloads = File("/home/daniel/Downloads")
        val cutter = FfmpegVideoCutter()
        val output = cutter.replaceAudio(File(downloads, "test.mp4"), File(downloads, "queen.mp4"), 10)
        println(output.absolutePath)
    }
}