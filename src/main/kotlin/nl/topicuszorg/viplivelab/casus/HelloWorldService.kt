package nl.topicuszorg.viplivelab.casus

import org.springframework.stereotype.Service

@Service
class HelloWorldService
{
    fun sayHello(naam: String, hoofdletters: Boolean): String
    {
        val antwoord = "Hello, $naam"
        return if (hoofdletters) antwoord.uppercase() else antwoord
    }
}
