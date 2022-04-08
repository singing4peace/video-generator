package de.singing4peace.videogenerator.access

import de.singing4peace.videogenerator.model.Recording
import de.singing4peace.videogenerator.model.VideoTrack
import org.springframework.data.jpa.repository.JpaRepository


interface RecordingRepository : JpaRepository<Recording, Long>

interface VideoTrackRepository : JpaRepository<VideoTrack, Long>