package net.andrecarbajal.domain

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class UrlService(private val urlRepository: UrlRepository) {

    @Value("\${app.base-url}")
    lateinit var baseUrl: String

    @Value("\${app.auth}")
    lateinit var authCode: String

    fun getAllUrls(): List<Url> {
        return urlRepository.findAll().reversed()
    }

}