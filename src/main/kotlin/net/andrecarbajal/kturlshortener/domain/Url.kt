package net.andrecarbajal.kturlshortener.domain

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import java.time.LocalDateTime

@Entity(name = "Url")
@Table(name = "url")
@AllArgsConstructor
@NoArgsConstructor
data class Url(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @JoinColumn(name = "original_url")
    var originalUrl: String? = null,

    @JoinColumn(name = "url_code")
    var urlCode: String? = null,

    @JoinColumn(name = "created_at")
    var createdAt: LocalDateTime? = null,

    @JoinColumn(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    var visits: Long? = null
) {
    fun incrementVisits() {
        this.visits = this.visits?.plus(1)
    }
}