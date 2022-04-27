package de.singing4peace.videogenerator

import de.singing4peace.videogenerator.access.RecordingRepository
import de.singing4peace.videogenerator.access.VideoManager
import de.singing4peace.videogenerator.access.VideoTrackRepository
import de.singing4peace.videogenerator.model.*
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import org.springframework.data.domain.ExampleMatcher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.annotation.Schedules
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.transaction.Transactional

private val logger = KotlinLogging.logger {}

@Component
class ScheduledTasks(
    val properties: ApplicationProperties,
    val trackRepository: VideoTrackRepository,
    val recordingRepository: RecordingRepository,
    val videoManager: VideoManager
) {
    @Scheduled(cron = "0 0 3 * * *")
    fun checkVideos() {
        logger.info { "Generating missing videos templates" }
        for (track in trackRepository.findAllByGeneratedTemplate(false)) {
            logger.info { "Generating missing video template for file ${track.fileName}" }
            videoManager.generateTemplateVideo(track)
            logger.info { "Generated missing video template for file ${track.fileName}" }
        }
        logger.info { "Generated missing videos templates" }


        logger.info { "Generating missing video segments" }
        for (track in trackRepository.findAllByGeneratedSegmentsAndGeneratedTemplate(generatedSegments = false, generatedTemplate = true)) {
            logger.info { "Generating missing video segments for file ${track.fileName}" }
            videoManager.generateSegments(track)
            logger.info { "Generated missing video segments for file ${track.fileName}" }
        }
        logger.info { "Generated missing videos segments" }
    }
}