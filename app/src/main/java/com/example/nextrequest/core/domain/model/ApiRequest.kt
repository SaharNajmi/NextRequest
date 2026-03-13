package com.example.nextrequest.core.domain.model

import androidx.compose.ui.graphics.ImageBitmap
import com.example.nextrequest.core.extensions.mapStringToKeyValuePairs
import com.example.nextrequest.core.models.HttpMethod
import com.example.nextrequest.core.models.KeyValue
import java.time.LocalDate

data class ApiRequest(
    val id: Int = 0,
    val requestUrl: String = "",
    val httpMethod: HttpMethod = HttpMethod.GET,
    val body: String? = null,
    val headers: List<KeyValue>? = null,
    val createdAt: LocalDate = LocalDate.now(),
) {
    val params: List<KeyValue>?
        get() = requestUrl.mapStringToKeyValuePairs()
    val baseUrl: String
        get() = requestUrl.substringBefore("?")
}

data class ApiResponse(
    val response: String,
    val statusCode: Int? = null,
    val imageResponse: ImageBitmap? = null,
)