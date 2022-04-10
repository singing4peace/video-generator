package de.singing4peace.videogenerator.controller

import de.singing4peace.videogenerator.access.CurrentVideoManager
import de.singing4peace.videogenerator.model.ApplicationProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.net.URI

@Controller
class VideoController(val currentVideoManager: CurrentVideoManager, val properties: ApplicationProperties) {

    @GetMapping("/current")
    fun currentVideo(): ResponseEntity<String> {
        val location = properties.videoUrlPrefix + currentVideoManager.currentId
        val headers = HttpHeaders()
        headers.location = URI(location)
        return ResponseEntity(headers, HttpStatus.TEMPORARY_REDIRECT)
    }

    @GetMapping("/video/{id}")
    fun specificVideo(@PathVariable id: String): ResponseEntity<String> {
        // TODO implement
        return ResponseEntity(HttpStatus.I_AM_A_TEAPOT)
    }
}