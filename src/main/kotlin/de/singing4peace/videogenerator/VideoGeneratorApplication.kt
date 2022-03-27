package de.singing4peace.videogenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VideoGeneratorApplication

fun main(args: Array<String>) {
	runApplication<VideoGeneratorApplication>(*args)
}
