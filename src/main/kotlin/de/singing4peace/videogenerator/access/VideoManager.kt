package de.singing4peace.videogenerator.access

import de.singing4peace.videogenerator.model.ApplicationProperties
import de.singing4peace.videogenerator.model.GeneratedVideo
import de.singing4peace.videogenerator.model.VideoTrack
import org.springframework.stereotype.Component
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Component
class VideoManager(
    val generatedVideoRepository: GeneratedVideoRepository,
    val videoTrackRepository: VideoTrackRepository,
    val properties: ApplicationProperties,
    val cutter: VideoCutter,
) {

    fun generateVideo(): File {
        val audioFile = File(properties.videoLibrary, properties.audioFileName)
        val audioLength = cutter.getDurationOfFile(audioFile)

        val neededSegments = (audioLength / properties.segmentLength).toInt()
        val tracks = mutableListOf<VideoTrack>()
        val trackCount = videoTrackRepository.count()
        for (i in 0 until neededSegments) {
            val trackIndex = Random.nextInt(0, trackCount.toInt())
            tracks.add(videoTrackRepository.getById(trackIndex.toLong()))
        }

        val generatedVideo = GeneratedVideo(UUID.randomUUID().toString(), tracks)
        return generateVideo(generatedVideo)
    }

    fun generateVideo(generatedVideo: GeneratedVideo): File {
        val cacheDir = File(properties.cacheDirectory)

        val introFile = File(properties.videoLibrary, properties.introFileName)
        val introLength = cutter.getDurationOfFile(introFile)

        val outroFile = File(properties.videoLibrary, properties.outroFileName)
        val outroLength = cutter.getDurationOfFile(outroFile)

        val audioFile = File(properties.videoLibrary, properties.audioFileName)
        val audioLength = cutter.getDurationOfFile(audioFile)

        // Check all versions
        generatedVideo.segments.forEach(::checkCachedVersions)

        val tracks = mutableListOf<String>()
        for ((i, track) in generatedVideo.segments.withIndex()) {
            val file = File(cacheDir, "${track.id}/$i.mp4")
            tracks.add(file.absolutePath)
        }

        val concatenated = cutter.concatenate(tracks)
        val concatenatedLength = cutter.getDurationOfFile(concatenated)
        val withAudio = cutter.replaceAudio(concatenated, audioFile, 0.0)

        val tmp = File("/tmp", UUID.randomUUID().toString() + ".mp4")
        val offsetTime = (introLength - properties.silenceBefore).coerceAtLeast(0.0)
        val duration = audioLength - (outroLength - properties.silenceAfter)
        cutter.cutFile(withAudio, tmp, 0, duration.toLong() * 1000, TimeUnit.MILLISECONDS)

        val output = cutter.concatenate(listOf(introFile, tmp, outroFile).map { file ->
            file.absolutePath
        }.toList())

        // Clean up tmp files
        tmp.delete()
        concatenated.delete()
        withAudio.delete()

        return output
    }

    fun checkCachedVersions(track: VideoTrack) {
        val cacheDir = File(properties.cacheDirectory)
        val formattedFile = File(cacheDir, "${track.id}.mp4")

        if (!formattedFile.exists() || !formattedFile.isFile) {
            val original = File(properties.videoLibrary, track.fileName)
            cutter.convertToH265FullHd60FPS(original, formattedFile)
        }

        val segmentDir = File(cacheDir, track.id.toString())
        if (!segmentDir.exists() || !segmentDir.isDirectory) {
            segmentDir.mkdirs()
            val template = segmentDir.absolutePath + "/%d.mp4"
            cutter.splitIntoSegments(formattedFile, template, properties.segmentLength, track.start)
        }
    }
}