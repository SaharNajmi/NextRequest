package com.example.nextrequest.home.data.repository

import com.example.nextrequest.core.KeyValueList
import com.example.nextrequest.core.domain.model.ApiResponse
import com.example.nextrequest.core.domain.repository.ApiService
import com.example.nextrequest.home.domain.repository.HomeRepository

class HomeRepositoryImp (private val apiService: ApiService) : HomeRepository{
    override suspend fun sendRequest(
        method: String,
        url: String,
        headers: KeyValueList?,
        parameters: KeyValueList?,
        body: Any?,
    ): ApiResponse {
       return apiService.sendRequest(method, url, headers, parameters, body)
    }
}