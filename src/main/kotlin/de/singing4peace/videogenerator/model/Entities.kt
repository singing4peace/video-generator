package de.singing4peace.videogenerator.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class GeneratedVideo(
    @Id
    var id: String,
    @OneToMany(cascade = [CascadeType.ALL])
    var segments: List<VideoTrack>,
)

@Entity
class CutVideoTrack(
    var start: Long,
    var length: Long,
    @ManyToOne
    var videoTrack: VideoTrack,
    @Id @GeneratedValue var id: Long? = null
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
    @ManyToOne
    var ensemble: Ensemble,
    @Id @GeneratedValue var id: Long? = null
)

enum class CameraAngle {
    WIDE_CENTER, WIDE_LEFT, WIDE_RIGHT, NEAR
}
