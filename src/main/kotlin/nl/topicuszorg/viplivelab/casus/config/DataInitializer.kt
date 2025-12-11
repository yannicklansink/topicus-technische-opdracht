package nl.topicuszorg.viplivelab.casus.config

import nl.topicuszorg.viplivelab.casus.dataaccess.AppointmentRepository
import nl.topicuszorg.viplivelab.casus.model.Appointment
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

@Component
class DataInitializer(
    private val appointmentRepository: AppointmentRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val nextWorkDay = findNextWorkDay(LocalDate.now())

        val appointments = listOf(
            Appointment(
                startTime = nextWorkDay.atTime(LocalTime.of(9, 0)),
                endTime = nextWorkDay.atTime(LocalTime.of(9, 30)),
                title = "Standup meeting",
                description = "Dagelijkse standup",
                personName = "Jan Jansen"
            ),
            Appointment(
                startTime = nextWorkDay.atTime(LocalTime.of(11, 0)),
                endTime = nextWorkDay.atTime(LocalTime.of(11, 30)),
                title = "Code review",
                description = null,
                personName = "Piet Pietersen"
            ),
            Appointment(
                startTime = nextWorkDay.atTime(LocalTime.of(14, 0)),
                endTime = nextWorkDay.atTime(LocalTime.of(14, 30)),
                title = "Sprint planning",
                description = "Planning voor sprint 5",
                personName = "Marie de Vries"
            )
        )

        appointmentRepository.saveAll(appointments)
    }

    private fun findNextWorkDay(from: LocalDate): LocalDate {
        var date = from
        while (date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY) {
            date = date.plusDays(1)
        }
        return date
    }
}
