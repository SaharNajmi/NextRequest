package com.example.nextrequest.history.data.mapper

import com.example.nextrequest.collection.domain.model.Request
import com.example.nextrequest.core.data.extensions.toByteArray
import com.example.nextrequest.core.data.extensions.toImageBitmap
import com.example.nextrequest.core.data.extensions.toLocalDate
import com.example.nextrequest.core.data.extensions.toLong
import com.example.nextrequest.history.data.entity.HistoryEntity
import com.example.nextrequest.history.domain.model.History

fun HistoryEntity.toDomain(): History {
    return History(
        id = id,
        requestUrl = requestUrl,
        httpMethod = this@toDomain.httpMethod,
        response = response,
        createdAt = createdAt.toLocalDate(),
        statusCode = statusCode,
        imageResponse = imageResponse?.toImageBitmap(),
        body = body,
        headers = headers
    )
}

fun History.toEntity(): HistoryEntity {
    return HistoryEntity(
        id = id,
        requestUrl = requestUrl,
        httpMethod = httpMethod,
        response = response,
        createdAt = createdAt.toLong(),
        statusCode = statusCode,
        imageResponse = imageResponse?.toByteArray(),
        body = body,
        headers = headers
    )
}

fun History.toRequest(): Request {
    return Request(
        requestName = "$httpMethod $requestUrl",
        requestUrl = requestUrl,
        httpMethod = httpMethod,
        response = response,
        imageResponse = imageResponse,
        createdAt = createdAt,
        statusCode = statusCode,
        body = body,
        headers = headers
    )
}

