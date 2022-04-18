package de.singing4peace.videogenerator.config

import de.singing4peace.videogenerator.access.GeneratedVideoRepository
import de.singing4peace.videogenerator.access.VideoManager
import de.singing4peace.videogenerator.model.GeneratedVideo
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

    @PostConstruct
    fun startYoutubeStream() {
        Thread {
            runBlocking {
                val channel = Channel<File>(5)

                launch {
                    while (true) {
                        val generatedVideo = videoManager.gatherGeneratedVideo()
                        println(generatedVideo.id)
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
        }.start()


    }
}