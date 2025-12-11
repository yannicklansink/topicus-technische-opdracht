package nl.topicuszorg.viplivelab.casus.dto

import java.time.LocalDateTime

data class AvailableSlotResponse(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)
