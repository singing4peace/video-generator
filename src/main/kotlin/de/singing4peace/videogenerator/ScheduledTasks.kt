package de.singing4peace.videogenerator

import de.singing4peace.videogenerator.access.RecordingRepository
import de.singing4peace.videogenerator.access.VideoManager
import de.singing4peace.videogenerator.access.VideoTrackRepository
import de.singing4peace.videogenerator.model.ApplicationProperties
import de.singing4peace.videogenerator.model.CutVideoTrack
import de.singing4peace.videogenerator.model.GeneratedVideo
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.transaction.Transactional

@Component
class ScheduledTasks(
    val properties: ApplicationProperties,
    val trackRepository: VideoTrackRepository,
    val recordingRepository: RecordingRepository,
    val videoManager: VideoManager
) {


   // @Scheduled(fixedRate = 3, timeUnit = TimeUnit.MINUTES)
    @Transactional
    @PostConstruct
    fun generateVideo() {
        val file = videoManager.generateVideo()
       println(file.path)
    }
}