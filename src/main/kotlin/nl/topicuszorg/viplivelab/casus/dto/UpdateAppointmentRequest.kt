package nl.topicuszorg.viplivelab.casus.dto

import java.time.LocalDateTime

data class UpdateAppointmentRequest(
    val startTime: LocalDateTime
)
