package nl.topicuszorg.viplivelab.casus.dto

import nl.topicuszorg.viplivelab.casus.model.Appointment
import java.time.LocalDateTime

data class AppointmentResponse(
    val id: Long,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val title: String,
    val description: String?,
    val personName: String
) {
    companion object {
        fun fromEntity(appointment: Appointment): AppointmentResponse {
            return AppointmentResponse(
                id = appointment.id!!,
                startTime = appointment.startTime,
                endTime = appointment.endTime,
                title = appointment.title,
                description = appointment.description,
                personName = appointment.personName
            )
        }
    }
}