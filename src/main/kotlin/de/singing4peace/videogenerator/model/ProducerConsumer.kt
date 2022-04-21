package de.singing4peace.videogenerator.model

import de.singing4peace.videogenerator.access.VideoCutter
import de.singing4peace.videogenerator.access.VideoManager
import java.io.File
import java.util.concurrent.BlockingQueue

data class Message(val file: File, val generatedVideo: GeneratedVideo)

class VideoProducer(private val blockingQueue: BlockingQueue<Message>, private val videoManager: VideoManager) : Runnable {

    @Volatile
    private var runFlag: Boolean = true

    private fun produce() {
        while (runFlag) {
            val generatedVideo = videoManager.gatherGeneratedVideo()
            val generatedFile = videoManager.generateVideo(generatedVideo)

            try {
                blockingQueue.put(Message(generatedFile, generatedVideo))
            } catch (e: InterruptedException) {
                break
            }
        }
    }

    fun stop() {
        runFlag = false
    }

    override fun run() {
        produce()
    } // Other methods
}

class StreamConsumer(
    private val blockingQueue: BlockingQueue<Message>,
    private val videoManager: VideoManager,
    private val cutter: VideoCutter,
    private val waitBetweenStreams: Int
) : Runnable {

    @Volatile
    private var runFlag: Boolean = true

    private fun consume() {
        while (true) {
            var message: Message?
            try {
                message = blockingQueue.take()
            } catch (e: InterruptedException) {
                break
            }

            val time = System.currentTimeMillis()
            val length = cutter.getDurationOfFile(message.file)
            videoManager.streamToYouTube(message.file)
            message.file.delete()
            val waitTime = (length * 1000 - (System.currentTimeMillis() - time)).coerceAtLeast(waitBetweenStreams.toDouble() * 1000)
            if (waitTime > 0) {
                Thread.sleep(waitTime.toLong())
            }
        }
    }

    fun stop() {
        runFlag = false
    }

    override fun run() {
        consume()
    } // Other methods
}
