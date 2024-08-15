package net.andrecarbajal.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UrlRepository : JpaRepository<Url, Long> {
    fun findByOriginalUrl(originalUrl: String): List<Url>
}