package de.singing4peace.videogenerator.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(
    val youtubeStreamKey: String, val audioFileName: String, val videoLibrary: String,
    val cacheDirectory: String, val introFileName: String, val outroFileName: String,
    val segmentLength: Int, val silenceBefore: Int, val silenceAfter: Int
)