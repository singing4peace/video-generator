package de.singing4peace.videogenerator.access

import de.singing4peace.videogenerator.model.GeneratedVideo
import de.singing4peace.videogenerator.model.Ensemble
import de.singing4peace.videogenerator.model.VideoTrack
import org.springframework.data.jpa.repository.JpaRepository


interface RecordingRepository : JpaRepository<Ensemble, Long>

interface VideoTrackRepository : JpaRepository<VideoTrack, Long> {
    fun findAllByGeneratedTemplate(generatedTemplate: Boolean): Iterable<VideoTrack>

    fun findAllByGeneratedSegmentsAndGeneratedTemplate(generatedSegments: Boolean, generatedTemplate: Boolean): Iterable<VideoTrack>

    fun findAllByGeneratedSegmentsAndGeneratedTemplateAndSegmentsBeforeAudioStartIsGreaterThanEqual(
        generatedSegments: Boolean,
        generatedTemplate: Boolean,
        segmentsBeforeAudioStart: Int
    ): List<VideoTrack>
}

interface GeneratedVideoRepository : JpaRepository<GeneratedVideo, Long>