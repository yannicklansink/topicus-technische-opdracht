package nl.topicuszorg.viplivelab.casus

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController("/hello")
class VoorbeeldController(
    private val helloWorldService: HelloWorldService
)
{
    @GetMapping("/{naam}")
    fun helloWorld(@PathVariable naam: String, @RequestParam hoofdletters: Boolean): String
    {
        return helloWorldService.sayHello(naam, hoofdletters)
    }

    @PostMapping
    fun doeIets(@RequestBody request: NaamDto)
    {
        println("Hello, ${request.voornaam} ${request.achternaam}")
    }
}

data class NaamDto(
    val voornaam: String,
    val achternaam: String
)
