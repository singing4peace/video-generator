package de.singing4peace.videogenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@ConfigurationPropertiesScan
@SpringBootApplication
@EnableScheduling
class VideoGeneratorApplication

fun main(args: Array<String>) {
	runApplication<VideoGeneratorApplication>(*args)
}
