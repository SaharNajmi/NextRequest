package com.example.nextrequest.home.domain.repository

import com.example.nextrequest.core.KeyValueList
import com.example.nextrequest.core.domain.model.ApiResponse

interface HomeRepository {
    suspend fun sendRequest(
        method: String,
        url: String,
        headers: KeyValueList? = null,
        parameters: KeyValueList? = null,
        body: Any? = null,
    ): ApiResponse
}