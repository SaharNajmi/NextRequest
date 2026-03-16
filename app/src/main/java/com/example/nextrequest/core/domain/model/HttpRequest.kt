package com.example.nextrequest.core.domain.model

import com.example.nextrequest.core.models.HttpMethod
import com.example.nextrequest.core.models.KeyValue

@Suppress("ArrayInDataClass")
data class HttpRequest(
    val requestUrl: String,
    val httpMethod: HttpMethod = HttpMethod.GET,
    val createdAt: Long = System.currentTimeMillis(),
    val response: String = "",
    val imageResponse: ByteArray? = null,
    val statusCode: Int? = null,
    val body: String? = null,
    val headers: List<KeyValue>? = null,
)