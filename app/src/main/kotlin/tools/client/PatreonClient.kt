package tools.client

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("https://www.patreon.com")
interface PatreonClient {
    @Get("/file")
    fun getFile(@QueryValue("h") h: String, @QueryValue("i") i: String, @Header(name = "cookie") cookie: String): String
}