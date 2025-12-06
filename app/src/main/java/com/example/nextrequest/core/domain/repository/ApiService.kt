package com.example.nextrequest.core.domain.repository

import com.example.nextrequest.core.KeyValueList
import com.example.nextrequest.core.domain.model.ApiResponse

interface ApiService {
    suspend fun sendRequest(
        method: String,
        url: String,
        headers: KeyValueList? = null,
        parameters: KeyValueList? = null,
        body: Any? = null,
    ): ApiResponse
}