package com.example.nextrequest.home.data.mapper

import com.example.nextrequest.collection.domain.model.Request
import com.example.nextrequest.core.data.extensions.toByteArray
import com.example.nextrequest.core.data.extensions.toImageBitmap
import com.example.nextrequest.core.data.extensions.toLong
import com.example.nextrequest.core.domain.model.ApiRequest
import com.example.nextrequest.core.domain.model.ApiResponse
import com.example.nextrequest.history.data.model.HttpRequest
import com.example.nextrequest.history.domain.model.HistoryItem

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

fun HistoryItem.Http.toHttpRequest(): ApiRequest {
    return ApiRequest(
        requestUrl = request.requestUrl,
        httpMethod = request.httpMethod,
        body = request.body,
        headers = request.headers
    )
}

fun HistoryItem.Http.toHttpResponse(): ApiResponse =
    ApiResponse(
        response = request.response,
        statusCode = request.statusCode,
        imageResponse = request.imageResponse?.toImageBitmap()
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

fun httpRequestToHistory(apiRequest: ApiRequest, apiResponse: ApiResponse): HistoryItem {
    val httpRequest = HttpRequest(
        requestUrl = apiRequest.requestUrl,
        httpMethod = apiRequest.httpMethod,
        body = apiRequest.body,
        headers = apiRequest.headers,
        response = apiResponse.response,
        statusCode = apiResponse.statusCode,
        imageResponse = apiResponse.imageResponse?.toByteArray(),
        createdAt = apiRequest.createdAt.toLong()
    )

    return HistoryItem.Http(id = 0, request = httpRequest)
}