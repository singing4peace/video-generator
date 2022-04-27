package de.singing4peace.videogenerator.config

import de.singing4peace.videogenerator.access.GeneratedVideoRepository
import de.singing4peace.videogenerator.access.VideoCutter
import de.singing4peace.videogenerator.access.VideoManager
import de.singing4peace.videogenerator.model.ApplicationProperties
import de.singing4peace.videogenerator.model.Message
import de.singing4peace.videogenerator.model.StreamConsumer
import de.singing4peace.videogenerator.model.VideoProducer
import org.springframework.context.annotation.Configuration
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingDeque
import javax.annotation.PostConstruct

@Configuration
class ServiceConfig(
    val videoManager: VideoManager,
    val cutter: VideoCutter,
    val properties: ApplicationProperties
) {

    @PostConstruct
    fun startYoutubeStream() {
        val blockingQueue: BlockingQueue<Message> = LinkedBlockingDeque(2)

        val videoProducer = VideoProducer(blockingQueue, videoManager)
        Thread(videoProducer).start()

        val streamConsumer = StreamConsumer(blockingQueue, videoManager, cutter, properties.waitBetweenStreams)
        Thread(streamConsumer).start()
    }
}