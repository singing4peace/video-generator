package de.singing4peace.videogenerator.config

import de.singing4peace.videogenerator.access.GeneratedVideoRepository
import de.singing4peace.videogenerator.access.VideoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import javax.annotation.PostConstruct

@Configuration
class ServiceConfig(
    val videoManager: VideoManager,
    val generatedVideoRepository: GeneratedVideoRepository
) {

    @Bean
    fun generatedVideoChannel(): Channel<File> {
        // We use a buffer capacity of five
        return Channel(5)
    }

    @PostConstruct
    fun startYoutubeStream(channel: Channel<File>) {
        runBlocking {
            launch {
                while (true) {
                    val generatedVideo = videoManager.gatherGeneratedVideo()
                    withContext(Dispatchers.IO) {
                        generatedVideoRepository.save(generatedVideo)
                    }
                    val generatedFile = videoManager.generateVideo(generatedVideo)
                    channel.send(generatedFile)
                }
            }

            launch {
                while (true) {
                    val generatedFile = channel.receive()
                    println(generatedFile.absolutePath)
                    videoManager.streamToYouTube(generatedFile)
                    println("Stream file ${generatedFile.absolutePath}")
                    withContext(Dispatchers.IO) {
                        generatedFile.delete()
                    }
                }
            }
        }
    }
}