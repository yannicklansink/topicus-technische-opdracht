package nl.topicuszorg.viplivelab.casus.applicationservices

import io.mockk.*
import nl.topicuszorg.viplivelab.casus.dataaccess.AppointmentRepository
import nl.topicuszorg.viplivelab.casus.dto.AppointmentRequest
import nl.topicuszorg.viplivelab.casus.exception.AppointmentNotFoundException
import nl.topicuszorg.viplivelab.casus.exception.AppointmentOverlapException
import nl.topicuszorg.viplivelab.casus.exception.AppointmentValidationException
import nl.topicuszorg.viplivelab.casus.model.Appointment
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AppointmentServiceTest {

    private lateinit var appointmentRepository: AppointmentRepository
    private lateinit var appointmentService: AppointmentService

    @BeforeEach
    fun setup() {
        appointmentRepository = mockk()
        appointmentService = AppointmentService(appointmentRepository)
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

    @Test
    fun `createAppointment should save and return appointment when valid`() {
        val request = AppointmentRequest(
            startTime = LocalDateTime.of(2025, 12, 15, 10, 0), // Maandag
            title = "Team Meeting",
            description = "Weekly standup",
            personName = "John Doe"
        )

        val savedAppointment = Appointment(
            id = 1L,
            startTime = request.startTime,
            endTime = request.startTime.plusMinutes(30),
            title = request.title,
            description = request.description,
            personName = request.personName
        )

        every {
            appointmentRepository.existsOverlappingAppointment(any(), any(), null)
        } returns false

        every {
            appointmentRepository.save(any())
        } returns savedAppointment

        val result = appointmentService.createAppointment(request)

        assertEquals(1L, result.id)
        assertEquals("Team Meeting", result.title)
        assertEquals(request.startTime, result.startTime)
        assertEquals(request.startTime.plusMinutes(30), result.endTime)

        verify(exactly = 1) {
            appointmentRepository.existsOverlappingAppointment(any(), any(), null)
        }
        verify(exactly = 1) {
            appointmentRepository.save(any())
        }
    }

    @Test
    fun `createAppointment should throw exception when weekend`() {
        val request = AppointmentRequest(
            startTime = LocalDateTime.of(2025, 12, 13, 10, 0),
            title = "Weekend Meeting",
            description = null,
            personName = "John Doe"
        )

        val exception = assertFailsWith<AppointmentValidationException> {
            appointmentService.createAppointment(request)
        }

        assertTrue(exception.message!!.contains("werkdagen"))

        verify(exactly = 0) {
            appointmentRepository.save(any())
        }
    }

    @Test
    fun `createAppointment should throw exception when overlap exists`() {
        val request = AppointmentRequest(
            startTime = LocalDateTime.of(2025, 12, 15, 10, 0),
            title = "Meeting",
            description = null,
            personName = "John Doe"
        )

        every {
            appointmentRepository.existsOverlappingAppointment(any(), any(), null)
        } returns true

        val exception = assertFailsWith<AppointmentOverlapException> {
            appointmentService.createAppointment(request)
        }

        assertTrue(exception.message!!.contains("al een afspraak"))

        verify(exactly = 0) {
            appointmentRepository.save(any())
        }
    }

    @Test
    fun `deleteAppointment should throw exception when not found`() {
        val appointmentId = 999L

        every {
            appointmentRepository.findById(appointmentId)
        } returns Optional.empty()

        val exception = assertFailsWith<AppointmentNotFoundException> {
            appointmentService.deleteAppointment(appointmentId)
        }

        assertTrue(exception.message!!.contains("niet gevonden"))

        verify(exactly = 0) {
            appointmentRepository.delete(any())
        }
    }

    @Test
    fun `findNextAvailableSlot should skip weekend to monday`() {
        val saturdayMorning = LocalDateTime.of(2025, 12, 13, 10, 0)

        every {
            appointmentRepository.findAllFromDateOrdered(any())
        } returns emptyList()

        val result = appointmentService.findNextAvailableSlot(saturdayMorning)

        // erwacht maandag 15 dec 2025, 09:00
        val expectedMonday = LocalDateTime.of(2025, 12, 15, 9, 0)
        assertEquals(expectedMonday, result.startTime)
        assertEquals(expectedMonday.plusMinutes(30), result.endTime)
    }
}
