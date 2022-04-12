package de.singing4peace.videogenerator.access

import java.io.File

interface VideoCutter {

    /**
     * Converts an input file to a h264, 60fps, 1920x1080 resolution video and outputs it to the output file.
     */
    fun convertToMp4H264FullHd60FPS(inputFile: File, outputFile: File) {
        convertToFormat(inputFile, outputFile, "libx264", "1920x1080", 60)
    }

    /**
     * Converts an input file to an output file with the specified codec, resolution and fps.
     * The actcual format will be inferred from the output file name
     */
    fun convertToFormat(inputFile: File, outputFile: File, codec: String, resolution: String, fps: Int)

    /**
     * Concatenates all video files in the list. All the files have to have the same format
     */
    fun concatenate(tracks: List<String>): File

    /**
     * Replaces the complete audio in the videoFile with the on from the audioFile
     * an additional offset in seconds can be specified.
     */
    fun replaceAudio(videoFile: File, audioFile: File, offset: Long = 0): File
}