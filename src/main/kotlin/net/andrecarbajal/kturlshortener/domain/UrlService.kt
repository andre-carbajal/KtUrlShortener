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
import java.util.regex.Pattern

@Service
class UrlService(private val urlRepository: UrlRepository) {

    @Value("\${app.base-url}")
    lateinit var baseUrl: String

    @Value("\${app.auth}")
    lateinit var authCode: String

    fun getAllUrls(): List<Url> {
        return urlRepository.findAll().reversed()
    }

    fun shortenUrl(originalUrl: String, urlCode: String, authInput: String): String {
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

        var code = urlCode
        if (urlCode.isEmpty()) {
            code = generateCode()
        }

        val url = Url(originalUrl = originalUrl, urlCode = code)
        urlRepository.save(url)

        return baseUrl + code
    }

    fun getOriginalUrl(urlCode: String): String? {
        val url: Url = urlRepository.findByUrlCode(urlCode)
            ?: throw UrlException.NotFoundException("URL not found")

        return url.originalUrl
    }

    fun updateUrlCode(auth: String, id: Long, data: Url): ResponseEntity<Void> {
        if (authCode != auth) {
            throw  UrlException.AuthException("Invalid auth code")
        }

        val url = urlRepository.findById(id).orElseThrow{UrlException.NotFoundException("URL not found")}

        if (data.originalUrl!!.isNotEmpty() && isNotValidUrl(data.originalUrl!!)) {
            return ResponseEntity.badRequest().build()
        }

        if (data.originalUrl!!.isNotEmpty()) {
            url.originalUrl = data.originalUrl
        }

        if (data.urlCode!!.isNotEmpty()) {
            url.urlCode = data.urlCode
        }

        urlRepository.save(url)
        return ResponseEntity.ok().build()
    }

    fun deleteUrl(auth: String, id: Long): ResponseEntity<Void> {
        if (authCode != auth) {
            throw UrlException.AuthException("Invalid auth code")
        }

        val url = urlRepository.findById(id).orElseThrow{UrlException.NotFoundException("URL not found")}
        urlRepository.delete(url)
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