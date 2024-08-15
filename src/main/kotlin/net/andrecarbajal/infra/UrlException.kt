package net.andrecarbajal.infra

sealed class UrlException(message: String) : RuntimeException(message) {
    class ValidationException(message: String) : UrlException(message)
    class AuthException(message: String) : UrlException(message)
}