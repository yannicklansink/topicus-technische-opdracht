package nl.topicuszorg.viplivelab.casus.dto

import java.time.LocalDateTime

data class AppointmentRequest(
    val startTime: LocalDateTime,
    val title: String,
    val description: String?,
    val personName: String
)
