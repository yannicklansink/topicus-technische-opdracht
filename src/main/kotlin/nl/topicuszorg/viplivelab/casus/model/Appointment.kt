package nl.topicuszorg.viplivelab.casus.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class Appointment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val title: String,
    val description: String?,
    val personName: String
)

