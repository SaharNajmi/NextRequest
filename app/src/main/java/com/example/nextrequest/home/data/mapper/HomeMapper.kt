package com.example.nextrequest.home.data.mapper

import com.example.nextrequest.collection.domain.model.Request
import com.example.nextrequest.core.domain.model.ApiRequest
import com.example.nextrequest.core.domain.model.ApiResponse
import com.example.nextrequest.history.domain.model.History

fun Request.toHttpRequest(): ApiRequest =
    ApiRequest(
        id = id,
        requestUrl = requestUrl ?: "",
        httpMethod = httpMethod,
        body = body,
        headers = headers
    )


fun Request.toHttpResponse(): ApiResponse =
    ApiResponse(
        response = response,
        statusCode = statusCode,
        imageResponse = imageResponse
    )

fun History.toHttpRequest(): ApiRequest =
    ApiRequest(
        id = id,
        requestUrl = requestUrl,
        httpMethod = httpMethod,
        body = body,
        headers = headers
    )


fun History.toHttpResponse(): ApiResponse =
    ApiResponse(
        response = response,
        statusCode = statusCode,
        imageResponse = imageResponse
    )

fun httpRequestToRequest(
    apiRequest: ApiRequest,
    apiResponse: ApiResponse,
): Request =
    Request(
        id = apiRequest.id,
        requestUrl = apiRequest.requestUrl,
        httpMethod = apiRequest.httpMethod,
        createdAt = apiRequest.createdAt,
        response = apiResponse.response,
        statusCode = apiResponse.statusCode,
        imageResponse = apiResponse.imageResponse,
        body = apiRequest.body,
        headers = apiRequest.headers
    )

fun httpRequestToHistory(apiRequest: ApiRequest, apiResponse: ApiResponse): History =
    History(
        requestUrl = apiRequest.requestUrl,
        httpMethod = apiRequest.httpMethod,
        createdAt = apiRequest.createdAt,
        response = apiResponse.response,
        statusCode = apiResponse.statusCode,
        imageResponse = apiResponse.imageResponse,
        body = apiRequest.body,
        headers = apiRequest.headers
    )