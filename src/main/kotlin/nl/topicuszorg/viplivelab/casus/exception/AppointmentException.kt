package nl.topicuszorg.viplivelab.casus.exception

class AppointmentNotFoundException(message: String) : RuntimeException(message)

class AppointmentValidationException(message: String) : RuntimeException(message)

class AppointmentOverlapException(message: String) : RuntimeException(message)

class AppointmentInPastException(message: String) : RuntimeException(message)
