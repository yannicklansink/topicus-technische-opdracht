package nl.topicuszorg.viplivelab.casus.exception

sealed class AppointmentException(
    val errorCode: String,
    message: String
) : RuntimeException(message)

class AppointmentNotFoundException(message: String)
    : AppointmentException("APPOINTMENT_NOT_FOUND", message)

class AppointmentValidationException(message: String)
    : AppointmentException("APPOINTMENT_VALIDATION_ERROR", message)

class AppointmentOverlapException(message: String)
    : AppointmentException("APPOINTMENT_OVERLAP", message)

class AppointmentInPastException(message: String)
    : AppointmentException("APPOINTMENT_IN_PAST", message)
