package de.singing4peace.videogenerator.model

import javax.persistence.*

@Entity
class GeneratedVideo(
    @Id
    var id: String,
    var audioStartTime: Int,
    @OneToMany(cascade = [CascadeType.ALL])
    var segments: List<CutVideoTrack>,
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
class Recording(
    @Column(unique = true)
    var organization: String,
    var languageCode: String,
    @Id @GeneratedValue var id: Long? = null
)

@Entity
class VideoTrack(
    var length: Int,
    var start: Int,
    var fileName: String,
    var cameraAngle: CameraAngle,
    @ManyToOne
    var recording: Recording,
    @Id @GeneratedValue var id: Long? = null
)

enum class CameraAngle {
    WIDE_CENTER, WIDE_LEFT, WIDE_RIGHT, NEAR
}
