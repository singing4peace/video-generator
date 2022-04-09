package de.singing4peace.videogenerator.access

import java.io.File

interface VideoCutter {

    fun concatenate(tracks: List<String>): File

    fun replaceAudio(videoFile: File, audioFile: File, offset: Long): File
}