package nl.topicuszorg.viplivelab.casus

import org.springframework.stereotype.Service

@Service
class HelloWorldService
{
    fun sayHello(naam: String): String
    {
        return ("Hello, $naam")
    }
}
