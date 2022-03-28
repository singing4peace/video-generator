package de.singing4peace.videogenerator

import de.singing4peace.videogenerator.model.ApplicationProperties
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ScheduledTasks(
    val properties: ApplicationProperties
) {


    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    fun generateVideo() {
    }
}