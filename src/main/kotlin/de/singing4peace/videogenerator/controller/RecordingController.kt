package de.singing4peace.videogenerator.controller

import de.singing4peace.videogenerator.access.RecordingRepository
import de.singing4peace.videogenerator.model.Ensemble
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/")
class RecordingController(val recordingRepository: RecordingRepository) {

    @GetMapping("/ensembles")
    fun getEnsembles(): List<Ensemble> {
        return recordingRepository.findAll()
    }
}