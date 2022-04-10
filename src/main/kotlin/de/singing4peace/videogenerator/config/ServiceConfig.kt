package de.singing4peace.videogenerator.config

import de.singing4peace.videogenerator.access.CurrentVideoManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfig {

    val currentVideoManager = CurrentVideoManager()

    @Bean
    fun currentVideoManager(): CurrentVideoManager {
        return currentVideoManager
    }
}