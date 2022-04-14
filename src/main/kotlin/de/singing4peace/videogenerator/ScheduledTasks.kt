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
        

        // Cut videos
    }
}