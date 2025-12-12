package nl.topicuszorg.viplivelab.casus.dto

import java.time.LocalDateTime

data class ErrorResponse(
    val status: Int,
    val error: String,
    val code: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
