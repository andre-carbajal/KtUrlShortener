package net.andrecarbajal.kturlshortener.infra

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ErrorHandling {
    @ExceptionHandler(UrlException.ValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidUrlException(e: UrlException.ValidationException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(e.message)
    }

    @ExceptionHandler(UrlException.AuthException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthException(e: UrlException.AuthException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.message)
    }
}