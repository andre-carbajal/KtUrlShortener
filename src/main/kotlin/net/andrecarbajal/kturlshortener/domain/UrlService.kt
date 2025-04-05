package net.andrecarbajal.kturlshortener.domain

import net.andrecarbajal.kturlshortener.infra.UrlException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.regex.Pattern

@Service
class UrlService(private val urlRepository: UrlRepository) {

    @Value("\${app.base-url}")
    lateinit var baseUrl: String

    @Value("\${app.auth}")
    lateinit var authCode: String

    fun shortenUrl(originalUrl: String, codeInput: String, authInput: String): String {
        if (authCode != authInput) {
            throw UrlException.AuthException("Invalid auth code")
        }

        if (isNotValidUrl(originalUrl)) {
            throw UrlException.ValidationException("Invalid URL format")
        }

        val existingUrls: List<Url> = urlRepository.findByOriginalUrl(originalUrl)
        if (existingUrls.isNotEmpty()) {
            return baseUrl + existingUrls.first().urlCode
        }

        var code = codeInput
        if (codeInput.isEmpty()) {
            code = generateCode()
        }

        val url = Url(originalUrl = originalUrl, urlCode = code)
        urlRepository.save(url)

        return baseUrl + code
    }

    fun getOriginalUrl(shortUrl: String): String? {
        val url: Url = urlRepository.findByUrlCode(shortUrl)
            ?: return null

        url.incrementVisits()
        urlRepository.save(url)
        return url.originalUrl
    }

    fun getUrlStats(urlCode: String): Url? {
        return urlRepository.findByUrlCode(urlCode)
    }

    fun getAllUrls(): List<Url> {
        return urlRepository.findAll().reversed()
    }

    fun deleteUrl(authInput: String, urlCode: String): ResponseEntity<Void> {
        if (authCode != authInput) {
            throw UrlException.AuthException("Invalid auth code")
        }

        val url: Url = urlRepository.findByUrlCode(urlCode)
            ?: return ResponseEntity.notFound().build()

        urlRepository.delete(url)
        return ResponseEntity.noContent().build()
    }

    fun updateUrlCode(authInput: String, urlCode: String, data: Url): ResponseEntity<Void> {
        if (authCode != authInput) {
            throw UrlException.AuthException("Invalid auth code")
        }

        val url: Url = urlRepository.findByUrlCode(urlCode)
            ?: return ResponseEntity.notFound().build()

        url.originalUrl = data.originalUrl
        url.updatedAt = LocalDateTime.now()

        urlRepository.save(url)
        return ResponseEntity.ok().build()
    }

    private fun isNotValidUrl(url: String): Boolean {
        val urlRegex = "^(https?|ftp)://([a-zA-Z0-9.-]+)(:[0-9]+)?(/.*)?$"
        val urlPattern: Pattern = Pattern.compile(urlRegex)

        if (!urlPattern.matcher(url).matches()) {
            return true
        }

        try {
            val urlObj: URL = URI(url).toURL()
            val protocol: String? = urlObj.protocol
            val host: String? = urlObj.host

            if (protocol == null || host.isNullOrEmpty()) {
                return true
            }

            val hostParts = host.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return hostParts.size < 2 || hostParts[0].isEmpty() || hostParts[hostParts.size - 1].isEmpty()
        } catch (e: MalformedURLException) {
            return true
        } catch (e: URISyntaxException) {
            return true
        }
    }

    private fun generateCode(): String {
        val charSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val codeLength = 6
        val code = StringBuilder(codeLength)
        for (i in 0 until codeLength) {
            code.append(charSet[SecureRandom().nextInt(charSet.length)])
        }
        return code.toString()
    }
}