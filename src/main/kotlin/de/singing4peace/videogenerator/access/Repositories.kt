package de.singing4peace.videogenerator.access

import de.singing4peace.videogenerator.model.GeneratedVideo
import de.singing4peace.videogenerator.model.Ensemble
import de.singing4peace.videogenerator.model.VideoTrack
import org.springframework.data.jpa.repository.JpaRepository


interface RecordingRepository : JpaRepository<Ensemble, Long>

interface VideoTrackRepository : JpaRepository<VideoTrack, Long>

interface GeneratedVideoRepository : JpaRepository<GeneratedVideo, Long>