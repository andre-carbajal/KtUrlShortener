package net.andrecarbajal.kturlshortener.controller

import jakarta.servlet.http.HttpServletResponse
import lombok.AllArgsConstructor
import net.andrecarbajal.kturlshortener.domain.UrlService
import net.andrecarbajal.kturlshortener.infra.UrlException
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.io.IOException

@Controller
@AllArgsConstructor
class UrlControllerUi(private val urlService: UrlService) {

    @GetMapping("/")
    fun getAllUrls(model: Model): String {
        val urls = urlService.getAllUrls().map { url ->
            url.apply {
                createdAtFormatted = createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
            }
        }
        model.addAttribute("urlHistory", urls)
        model.addAttribute("baseUrl", urlService.baseUrl)
        return "index"
    }

    @PostMapping("/ui/urls")
    @Transactional
    fun shortenUrl(
        @RequestParam("originalUrl") originalUrl: String,
        @RequestParam("urlCode") urlCode: String,
        @RequestParam("authInput") authInput: String,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            val shortUrl = urlService.shortenUrl(originalUrl, urlCode, authInput)
            redirectAttributes.addFlashAttribute("shortUrl", shortUrl)
        } catch (e: UrlException.AuthException) {
            redirectAttributes.addFlashAttribute("errorMessage", e.message)
        } catch (e: UrlException.ValidationException) {
            redirectAttributes.addFlashAttribute("errorMessage", e.message)
        }

        return "redirect:/"
    }

    @GetMapping("/{urlCode}")
    @Throws(IOException::class)
    fun getOriginalUrl(@PathVariable urlCode: String, response: HttpServletResponse) {
        val originalUrl = urlService.getOriginalUrl(urlCode)
        if (originalUrl != null) {
            response.sendRedirect(originalUrl)
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND)
        }
    }
}