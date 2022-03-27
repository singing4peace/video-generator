package de.singing4peace.videogenerator.model

import javax.persistence.*

@Entity
class GeneratedVideo(
    @Id
    var id: String,
    @OneToMany(cascade = [CascadeType.ALL])
    var segments: List<VideoTrack>,
)

@Entity
class Recording(
    var organization: String,
    var languageCode: String,
    @Id @GeneratedValue var id: Long? = null
)

@Entity
class VideoTrack(
    var length: Int,
    var start: Int,
    var cameraAngle: CameraAngle,
    @ManyToOne
    var recording: Recording,
    @Id @GeneratedValue var id: Long? = null
)

enum class CameraAngle {
    WIDE_CENTER, WIDE_LEFT, WIDE_RIGHT, NEAR
}
