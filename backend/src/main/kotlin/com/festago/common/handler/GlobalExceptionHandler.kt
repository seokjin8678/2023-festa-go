package com.festago.common.handler

import com.festago.auth.domain.authentication.AuthenticateContext
import com.festago.common.exception.BadRequestException
import com.festago.common.exception.ErrorCode
import com.festago.common.exception.FestaGoException
import com.festago.common.exception.ForbiddenException
import com.festago.common.exception.InternalServerException
import com.festago.common.exception.NotFoundException
import com.festago.common.exception.TooManyRequestException
import com.festago.common.exception.UnauthorizedException
import com.festago.common.exception.UnexpectedException
import com.festago.common.exception.ValidException
import com.festago.common.exception.dto.ErrorResponse
import com.festago.common.exception.dto.ValidErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.apache.catalina.connector.ClientAbortException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.InvalidMediaTypeException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

private val log: Logger = LoggerFactory.getLogger("ErrorLogger")

@RestControllerAdvice
class GlobalExceptionHandler(
    private val authenticateContext: AuthenticateContext,
) : ResponseEntityExceptionHandler() {


    @ExceptionHandler(ClientAbortException::class)
    fun handle(e: ClientAbortException?): ResponseEntity<ErrorResponse> {
        return ResponseEntity.badRequest().build()
    }

    @ExceptionHandler(InvalidMediaTypeException::class)
    fun handle(e: InvalidMediaTypeException?): ResponseEntity<ErrorResponse> {
        return ResponseEntity.badRequest().build()
    }

    @ExceptionHandler(ValidException::class)
    fun handle(e: ValidException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.badRequest()
            .body(ErrorResponse.from(e))
    }

    @ExceptionHandler(UnexpectedException::class)
    fun handle(e: UnexpectedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logError(e, request)
        return ResponseEntity.internalServerError()
            .body(ErrorResponse.from(e))
    }

    @ExceptionHandler(BadRequestException::class)
    fun handle(e: BadRequestException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logInfo(e, request)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.from(e))
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handle(e: UnauthorizedException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logInfo(e, request)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse.from(e))
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handle(e: ForbiddenException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logInfo(e, request)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.from(e))
    }

    @ExceptionHandler(NotFoundException::class)
    fun handle(e: NotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logInfo(e, request)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.from(e))
    }

    @ExceptionHandler(TooManyRequestException::class)
    fun handle(e: TooManyRequestException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logInfo(e, request)
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(ErrorResponse.from(e))
    }

    @ExceptionHandler(InternalServerException::class)
    fun handle(e: InternalServerException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logWarn(e, request)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR))
    }

    @ExceptionHandler(Exception::class)
    fun handle(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logError(e, request)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR))
    }

    override fun handleMethodArgumentNotValid(
        e: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ValidErrorResponse.from(e))
    }

    private fun logInfo(e: FestaGoException, request: HttpServletRequest) {
        log.info(
            LOG_FORMAT_INFO, request.method, request.requestURI, authenticateContext.id,
            authenticateContext.role, e.errorCode, e.javaClass.name, e.message
        )
    }

    private fun logWarn(e: FestaGoException, request: HttpServletRequest) {
        log.warn(
            LOG_FORMAT_WARN, request.method, request.requestURI,
            authenticateContext.id, authenticateContext.role, e
        )
    }

    private fun logError(e: Exception, request: HttpServletRequest) {
        log.error(
            LOG_FORMAT_ERROR, request.method, request.requestURI,
            authenticateContext.id, authenticateContext.role, e
        )
    }

    companion object {
        private const val LOG_FORMAT_INFO = "\n[🔵INFO] - ({} {})\n(id: {}, role: {})\n{}\n {}: {}"
        private const val LOG_FORMAT_WARN = "\n[🟠WARN] - ({} {})\n(id: {}, role: {})"
        private const val LOG_FORMAT_ERROR = "\n[🔴ERROR] - ({} {})\n(id: {}, role: {})"

        // INFO
        /*
           [🔵INFO] - (POST /admin/info)
           (id: 1, role: MEMBER)
           FOR_TEST_ERROR
            com.festago.exception.BadRequestException: 테스트용 에러입니다.
        */
        // WARN
        /*
           [🟠WARN] - (POST /admin/warn)
           (id: 1, role: MEMBER)
           FOR_TEST_ERROR
            com.festago.exception.InternalServerException: 테스트용 에러입니다.
             at com.festago.admin.presentation.AdminController.getWarn(AdminController.java:129)
        */
        // ERROR
        /*
           [🔴ERROR] - (POST /admin/error)
           (id: 1, role: MEMBER)
            java.lang.IllegalArgumentException: 테스트용 에러입니다.
             at com.festago.admin.presentation.AdminController.getError(AdminController.java:129)
        */
    }
}
