package nl.topicuszorg.viplivelab.casus.api

import nl.topicuszorg.viplivelab.casus.applicationservices.AppointmentService
import nl.topicuszorg.viplivelab.casus.dto.AppointmentRequest
import nl.topicuszorg.viplivelab.casus.dto.AppointmentResponse
import nl.topicuszorg.viplivelab.casus.dto.AvailableSlotResponse
import nl.topicuszorg.viplivelab.casus.dto.UpdateAppointmentRequest
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/appointments")
class AppointmentApi(
    private val appointmentService: AppointmentService
) {

    @PostMapping
    fun createAppointment(@RequestBody request: AppointmentRequest): ResponseEntity<AppointmentResponse> {
        val response = appointmentService.createAppointment(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getAppointments(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?
    ): ResponseEntity<List<AppointmentResponse>> {
        val appointments = if (date != null) {
            appointmentService.getAppointmentsByDate(date)
        } else {
            appointmentService.getAllAppointments()
        }
        return ResponseEntity.ok(appointments)
    }

    @GetMapping("/{id}")
    fun getAppointmentById(@PathVariable id: Long): ResponseEntity<AppointmentResponse> {
        val response = appointmentService.getAppointmentById(id)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateAppointment(
        @PathVariable id: Long,
        @RequestBody request: UpdateAppointmentRequest
    ): ResponseEntity<AppointmentResponse> {
        val response = appointmentService.updateAppointment(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteAppointment(@PathVariable id: Long): ResponseEntity<Void> {
        appointmentService.deleteAppointment(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/next-available")
    fun getNextAvailableSlot(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) from: LocalDateTime?
    ): ResponseEntity<AvailableSlotResponse> {
        val response = appointmentService.findNextAvailableSlot(from)
        return ResponseEntity.ok(response)
    }
}
