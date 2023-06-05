package data

import java.net.HttpURLConnection
import java.net.URL

object Request {
    data class Response(
        val statusCode: Int,
        val headers: Map<String, List<String>>? = null,
        val body: String? = null
    )

    fun sendRequest(
        url: String,
        method: String = "GET",
        headers: Map<String, String>? = null,
        body: String? = null
    ): Response
    {
        val conn = URL(url).openConnection() as HttpURLConnection

        with(conn) {
            requestMethod = method
            doOutput = body != null
            headers?.forEach(this::setRequestProperty)
        }

        if(body != null) {
            conn.outputStream.use {
                it.write(body.toByteArray())
            }
        }

        val responseBody = conn.inputStream.use { it.readBytes() }.toString(Charsets.UTF_8)

        return Response(conn.responseCode, conn.headerFields, responseBody)
    }
}