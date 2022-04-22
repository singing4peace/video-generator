package de.singing4peace.videogenerator.access

import com.google.common.base.Preconditions
import de.singing4peace.videogenerator.model.ApplicationProperties
import de.singing4peace.videogenerator.model.CameraAngle
import de.singing4peace.videogenerator.model.GeneratedVideo
import de.singing4peace.videogenerator.model.VideoTrack
import org.springframework.stereotype.Component
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.random.Random

@Component
class VideoManager(
    val videoTrackRepository: VideoTrackRepository,
    val properties: ApplicationProperties,
    val cutter: VideoCutter,
) {

    fun gatherGeneratedVideo(): GeneratedVideo {
        val audioFile = File(properties.videoLibrary, properties.audioFileName)
        val audioLength = cutter.getDurationOfFile(audioFile)

        val introLength = cutter.getDurationOfFile(File(properties.videoLibrary, properties.introFileName))

        val remainingIntroLength = (properties.audioPreludeDuration - introLength).coerceAtLeast(0.0)
        val neededSegmentsBefore = ceil(remainingIntroLength / properties.segmentLength.toDouble()).toInt()

        val neededSegmentsAfter = ceil((audioLength - properties.audioPreludeDuration) / properties.segmentLength).toInt()
        val tracks = mutableListOf<VideoTrack>()

        if (neededSegmentsBefore > 0) {
            val potentialVideosBefore =
                videoTrackRepository.findAllByGeneratedSegmentsAndGeneratedTemplateAndSegmentsBeforeAudioStartIsGreaterThanEqual(
                    generatedSegments = true,
                    generatedTemplate = true,
                    segmentsBeforeAudioStart = neededSegmentsBefore
                )

            for (i in 0 until neededSegmentsBefore) {
                val trackIndex = Random.nextInt(0, potentialVideosBefore.size)
                tracks.add(potentialVideosBefore[trackIndex])
            }
        }

        val potentialVideosAfter = videoTrackRepository.findAllByGeneratedSegmentsAndGeneratedTemplateAndSegmentsBeforeAudioStartIsGreaterThanEqual(
            generatedSegments = true,
            generatedTemplate = true,
            segmentsBeforeAudioStart = 0
        )
        for (i in 0 until neededSegmentsAfter) {
            // Make first shot a wide center shot
            if (i == 0) {
                val centerVideos = potentialVideosAfter.stream()
                    .filter { it.cameraAngle == CameraAngle.WIDE_CENTER }
                    .toList()

                val trackIndex = Random.nextInt(0, centerVideos.size)
                tracks.add(centerVideos[trackIndex])
            } else {
                val trackIndex = Random.nextInt(0, potentialVideosAfter.size)
                tracks.add(potentialVideosAfter[trackIndex])
            }
        }


        return GeneratedVideo(UUID.randomUUID().toString(), neededSegmentsBefore, tracks)
    }

    fun generateVideo(generatedVideo: GeneratedVideo): File {
        val cacheDir = File(properties.cacheDirectory)

        val introFile = File(properties.videoLibrary, properties.introFileName)
        val introLength = cutter.getDurationOfFile(introFile)

        val outroFile = File(properties.videoLibrary, properties.outroFileName)
        val outroLength = cutter.getDurationOfFile(outroFile)

        val audioFile = File(properties.videoLibrary, properties.audioFileName)
        val audioLength = cutter.getDurationOfFile(audioFile)

        assert(outroLength > -properties.outroOffset)

        val tracks = mutableListOf<String>()
        for ((i, track) in generatedVideo.segments.withIndex()) {
            val index = i + track.segmentsBeforeAudioStart - generatedVideo.segmentsBeforeAudioStart
            val file = File(cacheDir, "${track.id}/$index.mp4")
            tracks.add(file.absolutePath)
        }

        val concatenated = cutter.concatenate(tracks)
        val concatenatedLength = cutter.getDurationOfFile(concatenated)

        val tmp = File("/tmp", UUID.randomUUID().toString() + ".mp4")
        cutter.cutFileFast(concatenated, tmp, 0, (concatenatedLength + properties.outroOffset).toLong() * 1000, TimeUnit.MILLISECONDS)

        val output = cutter.concatenate(listOf(introFile, tmp, outroFile).map { file ->
            file.absolutePath
        }.toList())

        // How much time we should remove to make enough space for the intro
        val durationBeforeAudioStart = generatedVideo.segmentsBeforeAudioStart * properties.segmentLength
        val offsetTime =
            (introLength - durationBeforeAudioStart - properties.audioPreludeDuration).coerceAtLeast(0.0)

        // Add silence to video so that youtube does not stop our stream
        val currentLength = cutter.getDurationOfFile(output)
        val silence = currentLength - offsetTime - audioLength
        val paddedAudio = cutter.addSilenceToAudio(audioFile, silence)

        val withAudio = cutter.replaceAudio(output, paddedAudio, offsetTime)

        // Clean up tmp files
        tmp.delete()
        concatenated.delete()
        output.delete()
        paddedAudio.delete()

        return withAudio
    }

    fun generateTemplateVideo(track: VideoTrack) {
        val cacheDir = File(properties.cacheDirectory)
        val formattedFile = File(cacheDir, "${track.id}.mp4")

        val original = File(properties.videoLibrary, track.fileName)
        cutter.convertToH264FullHd60FPS(original, formattedFile)

        track.generatedTemplate = true
        videoTrackRepository.save(track)
    }

    fun generateSegments(track: VideoTrack) {
        Preconditions.checkState(track.generatedTemplate)
        val cacheDir = File(properties.cacheDirectory)
        val formattedFile = File(cacheDir, "${track.id}.mp4")
        val segmentDir = File(cacheDir, track.id.toString())
        segmentDir.mkdirs()

        val startTime = if (track.start < properties.segmentLength) {
            track.start
        } else {
            track.segmentsBeforeAudioStart = (track.start / properties.segmentLength.toDouble()).toInt()
            track.start - track.segmentsBeforeAudioStart * properties.segmentLength
        }

        val template = segmentDir.absolutePath + "/%d.mp4"
        cutter.splitIntoSegments(formattedFile, template, properties.segmentLength, startTime)

        track.generatedSegments = true
        videoTrackRepository.save(track)
    }

    fun streamToYouTube(generatedFile: File) {
        cutter.streamToYouTube(generatedFile, properties.youtubeStreamKey)
    }
}