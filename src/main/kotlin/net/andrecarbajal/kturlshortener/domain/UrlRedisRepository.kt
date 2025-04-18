package net.andrecarbajal.kturlshortener.domain

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class UrlRedisRepository(private val redisTemplate: RedisTemplate<String, Any>) {

    private val urlCodePrefix = "url:code:"
    private val urlOriginalPrefix = "url:original:"
    private val urlVisitsPrefix = "url:visits:"
    private val urlCreatedAtPrefix = "url:created:"
    private val allUrlsKey = "urls:all"

    fun save(url: Url) {
        val urlCode = url.urlCode ?: throw IllegalArgumentException("URL code cannot be null")
        val originalUrl = url.originalUrl ?: throw IllegalArgumentException("Original URL cannot be null")

        // Store url code -> original url mapping
        redisTemplate.opsForValue().set("$urlCodePrefix$urlCode", originalUrl)

        // Store original url -> url code mapping (for checking duplicates)
        redisTemplate.opsForValue().set("$urlOriginalPrefix$originalUrl", urlCode)

        // Store visits count (initialize to 0)
        redisTemplate.opsForValue().set("$urlVisitsPrefix$urlCode", url.visits.toString())

        // Store creation timestamp
        redisTemplate.opsForValue().set("$urlCreatedAtPrefix$urlCode", url.createdAt.toString())

        // Add to the list of all URLs
        redisTemplate.opsForList().leftPush(allUrlsKey, urlCode)
    }

    fun findByUrlCode(urlCode: String): Url? {
        val originalUrl = redisTemplate.opsForValue().get("$urlCodePrefix$urlCode") as String? ?: return null
        val visitsStr = redisTemplate.opsForValue().get("$urlVisitsPrefix$urlCode") as String? ?: "0"
        val createdAtStr = redisTemplate.opsForValue().get("$urlCreatedAtPrefix$urlCode") as String?

        return Url(
            originalUrl = originalUrl,
            urlCode = urlCode,
            createdAt = createdAtStr?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now(),
            visits = visitsStr.toLongOrNull() ?: 0
        )
    }

    fun findByOriginalUrl(originalUrl: String): List<Url> {
        val urlCode = redisTemplate.opsForValue().get("$urlOriginalPrefix$originalUrl") as String? ?: return emptyList()
        return listOf(findByUrlCode(urlCode) ?: return emptyList())
    }

    fun incrementVisits(urlCode: String) {
        redisTemplate.opsForValue().increment("$urlVisitsPrefix$urlCode", 1)
    }

    fun delete(urlCode: String) {
        val originalUrl = redisTemplate.opsForValue().get("$urlCodePrefix$urlCode") as String? ?: return

        // Delete all keys associated with this URL
        redisTemplate.delete("$urlCodePrefix$urlCode")
        redisTemplate.delete("$urlOriginalPrefix$originalUrl")
        redisTemplate.delete("$urlVisitsPrefix$urlCode")
        redisTemplate.delete("$urlCreatedAtPrefix$urlCode")

        // Remove from the list of all URLs
        redisTemplate.opsForList().remove(allUrlsKey, 0, urlCode)
    }

    fun findAll(): List<Url> {
        val urlCodes = redisTemplate.opsForList().range(allUrlsKey, 0, -1) ?: return emptyList()
        return urlCodes.mapNotNull { urlCode -> findByUrlCode(urlCode as String) }
    }

    fun update(urlCode: String, newOriginalUrl: String) {
        val url = findByUrlCode(urlCode) ?: return

        // Delete old original URL mapping
        redisTemplate.delete("$urlOriginalPrefix${url.originalUrl}")

        // Update with new original URL
        redisTemplate.opsForValue().set("$urlCodePrefix$urlCode", newOriginalUrl)
        redisTemplate.opsForValue().set("$urlOriginalPrefix$newOriginalUrl", urlCode)
    }
}