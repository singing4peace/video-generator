package de.singing4peace.videogenerator.access

import de.singing4peace.videogenerator.model.ApplicationProperties
import de.singing4peace.videogenerator.model.GeneratedVideo
import org.springframework.stereotype.Component

@Component
class VideoManager(
    val generatedVideoRepository: GeneratedVideoRepository,
    val applicationProperties: ApplicationProperties,
) {

    fun generateVideo(generatedVideo: GeneratedVideo) {

    }

}