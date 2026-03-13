package com.example.nextrequest.core.domain.repository

import com.example.nextrequest.core.domain.model.ApiResponse
import com.example.nextrequest.core.models.KeyValue

interface ApiService {
    suspend fun sendRequest(
        method: String,
        url: String,
        headers: List<KeyValue>? = null,
        parameters: List<KeyValue>? = null,
        body: Any? = null,
    ): ApiResponse
}