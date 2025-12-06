package com.example.nextrequest.core.data.network

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.nextrequest.core.KeyValueList
import com.example.nextrequest.core.domain.model.ApiResponse
import com.example.nextrequest.core.domain.repository.ApiService
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

class ApiServiceImp(
    private val client: HttpClient,
) : ApiService {
    override suspend fun sendRequest(
        method: String,
        url: String,
        headers: KeyValueList?,
        parameters: KeyValueList?,
        body: Any?,
    ): ApiResponse {
        val httpMethod = HttpMethod.Companion.parse(method)

        val result = client.request(url) {
            this.method = httpMethod
            url {
                parameters?.forEach { (key, value) ->
                    this.parameters.append(key, value)
                }
            }

            headers?.forEach { (key, value) ->
                this.headers.append(key, value)
            }

            if (body != null && httpMethod !in listOf(HttpMethod.Companion.Get, HttpMethod.Companion.Head)) {
                setBody(body)
            }
        }
        return buildHttpResult(result)
    }

    private suspend fun buildHttpResult(result: HttpResponse): ApiResponse {
        val (responseBody, imageResponse) = buildResponseBody(result)
        return ApiResponse(
            response = responseBody,
            statusCode = result.status.value,
            imageResponse = imageResponse
        )
    }

    private suspend fun buildResponseBody(
        result: HttpResponse,
    ): Pair<String, ImageBitmap?> {
        var imageResponse: ImageBitmap? = null

        val statusCode = result.status.value
        val contentType = result.contentType()?.toString() ?: ""

        val responseBody = if (statusCode in 200..299) {
            when {
                contentType == "image/svg+xml" -> {
                    result.bodyAsText()
                }

                contentType.startsWith("image/") -> {
                    val imageBytes = result.readBytes()
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    imageResponse = bitmap.asImageBitmap()

                    "This is an image of type $contentType with ${imageBytes.size / 1024} KB."
                }

                else -> {
                    result.bodyAsText()
                }
            }
        } else {
            result.bodyAsText()
        }

        return Pair(responseBody, imageResponse)
    }
}