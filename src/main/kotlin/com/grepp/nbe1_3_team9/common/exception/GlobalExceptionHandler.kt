package com.grepp.nbe1_3_team9.common.exception

import com.grepp.nbe1_3_team9.common.exception.exceptions.AccountBookException
import com.grepp.nbe1_3_team9.common.exception.exceptions.ExchangeRateException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    /*
    데이터 바인딩 중 발생하는 에러 BindException 처리
    */
    @ExceptionHandler(BindException::class)
    fun bindException(e: BindException): ResponseEntity<ErrorResponse> {
        val errorMsg = e.bindingResult
            .fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        val error = ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.reasonPhrase, errorMsg)

        return ResponseEntity.badRequest().body(error)
    }

    /*
        @Valid 어노테이션을 사용한 DTO의 유효성 검사에서 예외가 발생한 경우
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errorMsg = e.bindingResult
            .fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        val error = ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.reasonPhrase, errorMsg)

        return ResponseEntity.badRequest().body(error)
    }

    // 클라이언트에 오류 메시지 보내줌. http status는 400
    @ExceptionHandler(ExchangeRateException::class)
    fun handleExchangeRateException(e: ExchangeRateException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.reasonPhrase, e.message ?: "")
        return ResponseEntity.badRequest().body(error)
    }

    @ExceptionHandler(AccountBookException::class)
    fun handleAccountBookException(e: AccountBookException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse.from(BAD_REQUEST.value(), BAD_REQUEST.reasonPhrase, e.message ?: "")
        return ResponseEntity.badRequest().body(error)
    }
}