package de.singing4peace.videogenerator

import de.singing4peace.videogenerator.access.RecordingRepository
import de.singing4peace.videogenerator.access.VideoTrackRepository
import de.singing4peace.videogenerator.model.ApplicationProperties
import de.singing4peace.videogenerator.model.CutVideoTrack
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit
import javax.transaction.Transactional

@Component
class ScheduledTasks(
    val properties: ApplicationProperties,
    val trackRepository: VideoTrackRepository,
    val recordingRepository: RecordingRepository
) {


    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    @Transactional
    fun generateVideo() {
        val totalLength = properties.audioLength + properties.silenceBefore + properties.silenceAfter
        val segmentCount = totalLength.floorDiv(properties.segmentLength)
        val videoCount = trackRepository.count()
        val random = Random()
        val videos = mutableListOf<CutVideoTrack>()

        // Select videos
        var offset = 0
        for (i in 0..segmentCount) {
            val videoId = random.nextInt(videoCount.toInt())
            val track = trackRepository.getById(videoId.toLong())
            var length = properties.segmentLength

            // This means, that this is the last segment
            if (offset + length - totalLength < properties.segmentLength) {
                length = totalLength - offset
            }

            val cutTrack = CutVideoTrack(0, length.toLong(), track)
            videos.add(cutTrack)

            offset += properties.segmentLength
        }

        // Cut videos
    }
}