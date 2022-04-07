package de.singing4peace.videogenerator.model

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(
    val videoUrlPrefix: String, val audioFilePath: String
)