package nl.topicuszorg.viplivelab.casus.dataaccess

import nl.topicuszorg.viplivelab.casus.model.Appointment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface AppointmentRepository : JpaRepository<Appointment, Long> {

    fun findByStartTimeBetween(start: LocalDateTime, end: LocalDateTime): List<Appointment>

    @Query("SELECT a FROM Appointment a WHERE a.startTime >= :from ORDER BY a.startTime")
    fun findAllFromDateOrdered(from: LocalDateTime): List<Appointment>

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM Appointment a
        WHERE a.startTime < :endTime AND a.endTime > :startTime
        AND (:excludeId IS NULL OR a.id != :excludeId)
    """)
    fun existsOverlappingAppointment(startTime: LocalDateTime, endTime: LocalDateTime, excludeId: Long?): Boolean
}
