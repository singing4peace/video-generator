package de.singing4peace.videogenerator.access

import java.io.File
import java.util.concurrent.TimeUnit

interface VideoCutter {


    /**
     * Determines the length of a video file in seconds
     */
    fun getDurationOfFile(inputFile: File): Double


    fun cutFile(inputFile: File, outputFile: File, start: Long = 0, duration: Long?, timeUnit: TimeUnit = TimeUnit.SECONDS)

    fun cutFileFast(inputFile: File, outputFile: File, start: Long = 0, duration: Long?, timeUnit: TimeUnit = TimeUnit.SECONDS)

    /**
     * Splits the input file into segments with the specified segment length. The outputFileNameTemplate gets formatted
     * with String.format() and an Int as input
     */
    fun splitIntoSegments(inputFile: File, outputFileNameTemplate: String, segmentLength: Int) {
        splitIntoSegments(inputFile, outputFileNameTemplate, segmentLength, 0.0)
    }

    fun splitIntoSegments(inputFile: File, outputFileNameTemplate: String, segmentLength: Int, offset: Double)


    /**
     * Converts an input file to an output file with the specified codec, resolution and fps.
     * The actcual format will be inferred from the output file name
     */
    fun convertToFormat(inputFile: File, outputFile: File, codec: String, resolution: String, pixelFormat: String, fps: Int)

    /**
     * Concatenates all video files in the list. All the files have to have the same format
     */
    fun concatenate(tracks: List<String>): File

    /**
     * Replaces the complete audio in the videoFile with the on from the audioFile
     * an additional offset in seconds can be specified.
     */
    fun replaceAudio(videoFile: File, audioFile: File, offset: Double = 0.0): File

    /**
     * Converts an input file to a h264, 60fps, 1920x1080 resolution video and outputs it to the output file.
     */
    fun convertToH264FullHd60FPS(inputFile: File, outputFile: File) {
        convertToFormat(inputFile, outputFile, "libx264", "1920x1080", "yuv420p",60)
    }

    fun streamToYouTube(generatedFile: File, streamKey: String);

}