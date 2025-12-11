package nl.topicuszorg.viplivelab.casus.applicationservices

import nl.topicuszorg.viplivelab.casus.dataaccess.AppointmentRepository
import nl.topicuszorg.viplivelab.casus.dto.AppointmentRequest
import nl.topicuszorg.viplivelab.casus.dto.AppointmentResponse
import nl.topicuszorg.viplivelab.casus.dto.AvailableSlotResponse
import nl.topicuszorg.viplivelab.casus.dto.UpdateAppointmentRequest
import nl.topicuszorg.viplivelab.casus.exception.AppointmentInPastException
import nl.topicuszorg.viplivelab.casus.exception.AppointmentNotFoundException
import nl.topicuszorg.viplivelab.casus.exception.AppointmentOverlapException
import nl.topicuszorg.viplivelab.casus.exception.AppointmentValidationException
import nl.topicuszorg.viplivelab.casus.model.Appointment
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class AppointmentService(
    private val appointmentRepository: AppointmentRepository
) {
    companion object {
        private val WORK_START = LocalTime.of(9, 0)
        private val WORK_END = LocalTime.of(17, 0)
        private val LAST_APPOINTMENT_START = LocalTime.of(16, 30)
        private const val APPOINTMENT_DURATION_MINUTES = 30L
        private val WORK_DAYS = setOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        ) // setof is uniek en immutable
    }

    fun createAppointment(request: AppointmentRequest): AppointmentResponse {
        val endTime = request.startTime.plusMinutes(APPOINTMENT_DURATION_MINUTES)

        validateAppointmentTime(request.startTime, endTime)
        validateNoOverlap(request.startTime, endTime, null)

        val appointment = Appointment(
            startTime = request.startTime,
            endTime = endTime,
            title = request.title,
            description = request.description,
            personName = request.personName
        )

        val saved = appointmentRepository.save(appointment)
        return AppointmentResponse.fromEntity(saved)
    }

    fun getAllAppointments(): List<AppointmentResponse> {
        return appointmentRepository.findAll()
            .sortedBy { it.startTime }
            .map { AppointmentResponse.fromEntity(it) }
    }

    fun getAppointmentsByDate(date: LocalDate): List<AppointmentResponse> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.plusDays(1).atStartOfDay()

        return appointmentRepository.findByStartTimeBetween(startOfDay, endOfDay)
            .sortedBy { it.startTime }
            .map { AppointmentResponse.fromEntity(it) }
    }

    fun getAppointmentById(id: Long): AppointmentResponse {
        val appointment = appointmentRepository.findById(id)
            .orElseThrow { AppointmentNotFoundException("Afspraak met id $id niet gevonden") }
        return AppointmentResponse.fromEntity(appointment)
    }

    fun updateAppointment(id: Long, request: UpdateAppointmentRequest): AppointmentResponse {
        val existing = appointmentRepository.findById(id)
            .orElseThrow { AppointmentNotFoundException("Afspraak met id $id niet gevonden") }

        if (existing.startTime.isBefore(LocalDateTime.now())) {
            throw AppointmentInPastException("Afspraken in het verleden kunnen niet worden gewijzigd")
        }

        val newEndTime = request.startTime.plusMinutes(APPOINTMENT_DURATION_MINUTES)

        validateAppointmentTime(request.startTime, newEndTime)
        validateNoOverlap(request.startTime, newEndTime, id)

        val updated = existing.copy(
            startTime = request.startTime,
            endTime = newEndTime
        )

        val saved = appointmentRepository.save(updated)
        return AppointmentResponse.fromEntity(saved)
    }

    fun deleteAppointment(id: Long) {
        val existing = appointmentRepository.findById(id)
            .orElseThrow { AppointmentNotFoundException("Afspraak met id $id niet gevonden") }

        if (existing.startTime.isBefore(LocalDateTime.now())) {
            throw AppointmentInPastException("Afspraken in het verleden kunnen niet worden verwijderd")
        }

        appointmentRepository.delete(existing)
    }

    fun findNextAvailableSlot(from: LocalDateTime?): AvailableSlotResponse {
        var candidate = from ?: LocalDateTime.now()

        candidate = adjustToNextValidSlot(candidate)

        val appointments = appointmentRepository.findAllFromDateOrdered(candidate)

        for (appointment in appointments) {
            val candidateEnd = candidate.plusMinutes(APPOINTMENT_DURATION_MINUTES)

            if (candidateEnd <= appointment.startTime) {
                return AvailableSlotResponse(candidate, candidateEnd)
            }

            candidate = adjustToNextValidSlot(appointment.endTime)
        }

        val candidateEnd = candidate.plusMinutes(APPOINTMENT_DURATION_MINUTES)
        return AvailableSlotResponse(candidate, candidateEnd)
    }

    private fun adjustToNextValidSlot(dateTime: LocalDateTime): LocalDateTime {
        var adjusted = dateTime

        while (!WORK_DAYS.contains(adjusted.dayOfWeek)) {
            adjusted = adjusted.plusDays(1).with(WORK_START)
        }

        if (adjusted.toLocalTime().isBefore(WORK_START)) {
            adjusted = adjusted.with(WORK_START)
        }

        if (adjusted.toLocalTime().isAfter(LAST_APPOINTMENT_START)) {
            adjusted = adjusted.plusDays(1).with(WORK_START)
            while (!WORK_DAYS.contains(adjusted.dayOfWeek)) {
                adjusted = adjusted.plusDays(1)
            }
        }

        return adjusted
    }

    private fun validateAppointmentTime(startTime: LocalDateTime, endTime: LocalDateTime) {
        if (!WORK_DAYS.contains(startTime.dayOfWeek)) {
            throw AppointmentValidationException("Afspraken kunnen alleen op werkdagen (ma-vr) worden gepland")
        }

        if (startTime.toLocalTime().isBefore(WORK_START)) {
            throw AppointmentValidationException("Afspraken kunnen niet voor 09:00 beginnen")
        }

        if (endTime.toLocalTime().isAfter(WORK_END)) {
            throw AppointmentValidationException("Afspraken moeten voor 17:00 eindigen")
        }
    }

    private fun validateNoOverlap(startTime: LocalDateTime, endTime: LocalDateTime, excludeId: Long?) {
        if (appointmentRepository.existsOverlappingAppointment(startTime, endTime, excludeId)) {
            throw AppointmentOverlapException("Er is al een afspraak gepland in dit tijdslot")
        }
    }
}
