package biz.gripcloud.admin.exception

import com.auth0.jwt.exceptions.JWTVerificationException
import mu.KotlinLogging
import my.kopring.setting.exception.BadRequestException
import my.kopring.setting.exception.EntityNotFoundException
import my.kopring.setting.exception.ErrorResponse
import my.kopring.setting.utils.RequestUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletRequest


private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalRestControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validationExceptionHandler(
        request: HttpServletRequest,
        ex: MethodArgumentNotValidException
    ): ResponseEntity<*>? {
        log.error(
            "[EXCEPTION] Method argument not valid. URI: [{}], method: [{}], error message: [{}], from: [{}]",
            request.requestURI, request.method, ex.message, RequestUtils.getClientIp(request),
            ex
        )

        val errorList: ArrayList<Map<String, String?>> = arrayListOf<Map<String, String?>>()
        ex.bindingResult.fieldErrors.forEach{ fieldError: FieldError ->
            val error: Map<String, String?> = mapOf(
                "field" to fieldError.field,
                "defaultMessage" to fieldError.defaultMessage
            )
            errorList.add(error)
        }

        return ResponseEntity<Any?>(
            ErrorResponse(
                status = 400,
                error = "Invalid argument.",
                errors = errorList,
                message = errorList[0]["defaultMessage"],
                path = request.requestURI
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun entityNotFoundExceptionHandler(
        request: HttpServletRequest,
        ex: EntityNotFoundException
    ) : ResponseEntity<*>? {
        log.error(
            "[EXCEPTION] Entity not found. URI: [{}], method: [{}], error message: [{}], from [{}]",
                request.requestURI, request.method, ex.message ?: "", RequestUtils.getClientIp(request),
            ex
        )

        return ResponseEntity<Any?>(
            ErrorResponse(
                status = 400,
                error = "Entity not found",
                errors = null,
                message = ex.message,
                path = request.requestURI
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(AuthenticationException::class, JWTVerificationException::class)
    fun authenticationExceptionHandler(request: HttpServletRequest, ex: Exception): ResponseEntity<*>? {
        log.warn(
            "Not authenticated. URI [{}], method: [{}], from [{}]",
            request.requestURI, request.method, RequestUtils.getClientIp(request),
            ex
        )

        return ResponseEntity<Any?>(
            ErrorResponse(
                status = 401,
                error = "Not authenticated",
                errors = null,
                message = ex.message,
                path = request.requestURI
            ),
            HttpStatus.UNAUTHORIZED
        )
    }

    @ExceptionHandler(BadRequestException::class)
    fun badRequestException(
        request: HttpServletRequest,
        ex: BadRequestException
    ): ResponseEntity<*>? {
        log.error(
            "[EXCEPTION] Bad request. URI: [{}], method: [{}], error message: [{}], from [{}]",
            request.requestURI, request.method, ex.message ?: "", RequestUtils.getClientIp(request),
            ex
        )

        return ResponseEntity<Any?>(
            ErrorResponse(
                status = 400,
                error = "Bad request",
                errors = null,
                message = ex.message,
                path = request.requestURI
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(Exception::class)
    fun exceptionHandler(request: HttpServletRequest, ex: Exception): ResponseEntity<*>? {
        log.error(
            "Internal server error occurred. URI [{}], method: [{}], from [{}]",
            request.requestURI, request.method, RequestUtils.getClientIp(request),
            ex
        )

        return ResponseEntity<Any?>(
            ErrorResponse(
                status = 500,
                error = "Internal server error",
                errors = null,
                message = ex.message,
                path = request.requestURI
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}