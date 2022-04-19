package de.singing4peace.videogenerator.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class GeneratedVideo(
    @Id
    val id: String,
    val segmentsBeforeAudioStart: Int,
    @OneToMany(cascade = [CascadeType.ALL])
    val segments: List<VideoTrack>,
)

@Entity
class Ensemble(
    @Column(unique = true)
    var name: String,
    var languageCode: String,
    @JsonIgnore
    @Id @GeneratedValue var id: Long? = null
)

@Entity
class VideoTrack(
    var start: Double,
    var fileName: String,
    var cameraAngle: CameraAngle,
    var generatedTemplate: Boolean = false,
    var generatedSegments: Boolean = false,
    var segmentsBeforeAudioStart: Int,
    @ManyToOne
    var ensemble: Ensemble,
    @Id @GeneratedValue var id: Long? = null
)

enum class CameraAngle {
    WIDE_CENTER, WIDE_LEFT, WIDE_RIGHT, NEAR, UNDEFINED
}
