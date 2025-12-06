package com.example.nextrequest.collection.data.mapper

import com.example.nextrequest.collection.data.entity.CollectionEntity
import com.example.nextrequest.collection.data.entity.RequestEntity
import com.example.nextrequest.collection.domain.model.Collection
import com.example.nextrequest.collection.domain.model.Request
import com.example.nextrequest.core.data.extensions.toByteArray
import com.example.nextrequest.core.data.extensions.toImageBitmap
import com.example.nextrequest.core.data.extensions.toLocalDate
import com.example.nextrequest.core.data.extensions.toLong

fun CollectionEntity.toDomain(requests: List<Request>): Collection {
    return Collection(
        collectionId = collectionId,
        collectionName = collectionName,
        requests = requests
    )
}

fun Collection.toEntity(): CollectionEntity {
    return CollectionEntity(
        collectionId = collectionId,
        collectionName = collectionName
    )
}

fun Request.toEntity(collectionId: String): RequestEntity {
    return RequestEntity(
        id = id,
        collectionId = collectionId,
        requestName = requestName,
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

fun Request.toEntity(
    collectionId: String,
    requestName: String,
): RequestEntity {
    return RequestEntity(
        id = id,
        collectionId = collectionId,
        requestName = requestName,
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


fun RequestEntity.toDomain(): Request {
    return Request(
        id = id,
        requestName = requestName,
        requestUrl = requestUrl,
        httpMethod = httpMethod,
        response = response,
        imageResponse = imageResponse?.toImageBitmap(),
        createdAt = createdAt.toLocalDate(),
        statusCode = statusCode,
        body = body,
        headers = headers
    )
}

