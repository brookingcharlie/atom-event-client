package com.example.demo

import com.rometools.rome.feed.atom.Feed
import com.rometools.rome.io.WireFeedInput
import com.rometools.rome.io.XmlReader
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.Base64
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource


@SpringBootApplication
class DemoApplication : CommandLineRunner {
    override fun run(vararg args: String) {
        val url = "http://localhost:8080/feed"
        val feed = WireFeedInput().build(XmlReader(URL(url))) as Feed
        println("Feed: ${feed.title}")

        for (entry in feed.entries) {
            println("Entry: ${entry.title}")

            val content = entry.contents[0]
            val base64Encoded: String = content.value
            val base64Decoded: ByteArray = Base64.getDecoder().decode(base64Encoded)
            val multipart = MimeMultipart(ByteArrayDataSource(base64Decoded, content.type))

            for (i in 0 until multipart.count) {
                val part = multipart.getBodyPart(i)
                val partBytes = ByteArrayOutputStream().also { part.inputStream.copyTo(it) }.toByteArray()
                val partString = String(partBytes, Charsets.UTF_8)
                println("Part ${i}: ${part.contentType}: ${partString}")
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
