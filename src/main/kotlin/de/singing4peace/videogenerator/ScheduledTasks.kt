package de.singing4peace.videogenerator

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ScheduledTasks {


    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    fun generateVideo() {

    }
}