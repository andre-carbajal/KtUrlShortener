package net.andrecarbajal.kturlshortener.controller

import jakarta.transaction.Transactional
import net.andrecarbajal.kturlshortener.domain.Url
import net.andrecarbajal.kturlshortener.domain.UrlService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*


@Controller
class UrController(private val urlService: UrlService) {

    @GetMapping("/api/urls")
    fun getAllUrls(): ResponseEntity<List<Url>> {
        return ResponseEntity.ok(urlService.getAllUrls())
    }

    @PutMapping("/api/urls/{id}")
    @Transactional
    fun updateUrl(@RequestHeader("Authorization") auth: String, @PathVariable id:Long, @RequestBody data: Url): ResponseEntity<Void>{
        return urlService.updateUrlCode(auth, id, data)
    }

    @DeleteMapping("/api/urls/{id}")
    @Transactional
    fun deleteUrl(@RequestHeader("Authorization") auth: String, @PathVariable id: Long): ResponseEntity<Void> {
        return urlService.deleteUrl(auth, id)
    }

}