package nl.topicuszorg.viplivelab.casus.exception

import nl.topicuszorg.viplivelab.casus.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AppointmentException::class)
    fun handleAppointmentException(ex: AppointmentException): ResponseEntity<ErrorResponse> {
        val status = when (ex) {
            is AppointmentNotFoundException -> HttpStatus.NOT_FOUND
            is AppointmentOverlapException -> HttpStatus.CONFLICT
            is AppointmentValidationException -> HttpStatus.BAD_REQUEST
            is AppointmentInPastException -> HttpStatus.BAD_REQUEST
        }

        return ResponseEntity.status(status).body(
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                code = ex.errorCode,
                message = ex.message ?: "Onbekende fout"
            )
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                code = "VALIDATION_ERROR",
                message = errors.ifEmpty { "Validatie gefaald" }
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        ex.printStackTrace()

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                code = "INTERNAL_ERROR",
                message = "Er is een onverwachte fout opgetreden"
            )
        )
    }
}
