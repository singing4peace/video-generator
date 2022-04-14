package de.singing4peace.videogenerator.access

import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.floor


@Component
class FfmpegVideoCutter : VideoCutter {

    // Paths on alpine linux which we use in the docker container
    val ffmpeg = FFmpeg("/usr/bin/ffmpeg")
    val ffprobe = FFprobe("/usr/bin/ffprobe")

    override fun getDurationOfFile(inputFile: File): Double {
        val result = ffprobe.probe(inputFile.absolutePath)
        var longest = 0.0
        for (stream in result.streams) {
            if (stream.duration > longest) {
                longest = stream.duration
            }
        }

        return longest
    }

    override fun cutFile(inputFile: File, outputFile: File, start: Long, duration: Long?, timeUnit: TimeUnit) {
        val outputBuilder = FFmpegBuilder()
            .setStartOffset(start, timeUnit)
            .addInput(inputFile.absolutePath)
            .addOutput(outputFile.absolutePath)
            .setVideoCodec("copy")
            .setStrict(FFmpegBuilder.Strict.NORMAL)

        if (duration != null) {
            outputBuilder.setDuration(duration, timeUnit)
        }

        val builder = outputBuilder.done()

        val executor = FFmpegExecutor(ffmpeg, ffprobe)
        val job = executor.createJob(builder)
        job.run()
    }

    override fun splitIntoSegments(inputFile: File, outputFileNameTemplate: String, segmentLength: Int) {
        val duration = getDurationOfFile(inputFile)
        // We use toInt as we want no segment to be shorter than the segment length,
        // So that in some cases the last segment has length: segmentLength + remaining (remaining < segmentLength)
        val segments = (duration / segmentLength).toInt()

        for (i in 0 until segments) {
            val outputFileName = outputFileNameTemplate.format(i)

            val seekingTime = i * segmentLength

            // We have not yet reached the last segment
            if (i < segments - 1) {
                cutFile(inputFile, File(outputFileName), seekingTime.toLong(), segmentLength.toLong())
            } else {
                cutFile(inputFile, File(outputFileName), seekingTime.toLong(), null)
            }
        }
    }

    override fun convertToFormat(inputFile: File, outputFile: File, codec: String, resolution: String, fps: Int) {
        val builder = FFmpegBuilder()
            .addInput(inputFile.absolutePath)
            .addOutput(outputFile.absolutePath)
            .setVideoCodec(codec)
            .setVideoResolution(resolution)
            .setVideoFrameRate(fps, 1)
            .setStrict(FFmpegBuilder.Strict.NORMAL).done()

        val executor = FFmpegExecutor(ffmpeg, ffprobe)
        val job = executor.createJob(builder)
        job.run()
    }

    @Throws(IOException::class)
    override fun concatenate(tracks: List<String>): File {
        val output = File("/tmp", UUID.randomUUID().toString() + ".mp4")

        val inputFileContent = StringBuilder()
        for (track in tracks) {
            inputFileContent.append("file ")
            inputFileContent.append("\'")
            inputFileContent.append(track)
            inputFileContent.append("\'\n")
        }

        val inputFile = File("/tmp", UUID.randomUUID().toString() + ".txt")
        inputFile.createNewFile()
        Files.writeString(Paths.get(inputFile.toURI()), inputFileContent.toString())

        // Concatenate videos
        val builder = FFmpegBuilder().setFormat("concat").addInput(inputFile.absolutePath).addExtraArgs("-safe", "0").addOutput(output.absolutePath)
            .setVideoCodec("libx264").addExtraArgs("-c", "copy").done()

        val executor = FFmpegExecutor(ffmpeg, ffprobe)
        val job = executor.createJob(builder)
        job.run()

        inputFile.delete()

        return output
    }

    override fun replaceAudio(videoFile: File, audioFile: File, offset: Double): File {
        val output = File("/tmp", UUID.randomUUID().toString() + ".mp4")

        val builder = FFmpegBuilder()
            .addExtraArgs("-itsoffset", "$offset")
            .addInput(audioFile.absolutePath)
            .addInput(videoFile.absolutePath)
            .addOutput(output.absolutePath)
            .addExtraArgs("-map", "1:v:0", "-map", "0:a:0", "-async", "1", "-c:v", "copy")
            .setStrict(FFmpegBuilder.Strict.NORMAL).done()

        val executor = FFmpegExecutor(ffmpeg, ffprobe)
        val job = executor.createJob(builder)
        job.run()

        return output
    }
}