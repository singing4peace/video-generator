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


@Component
class FfmpegVideoCutter : VideoCutter {

    // Paths on alpine linux which we use in the docker container
    val ffmpeg = FFmpeg("/usr/bin/ffmpeg")
    val ffprobe = FFprobe("/usr/bin/ffprobe")

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
        inputFile.createNewFile();
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

    override fun replaceAudio(videoFile: File, audioFile: File, offset: Long): File {
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